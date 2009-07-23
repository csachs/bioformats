//
// APLReader.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats.in;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import loci.common.Location;
import loci.common.RandomAccessInputStream;
import loci.formats.CoreMetadata;
import loci.formats.FormatException;
import loci.formats.FormatReader;
import loci.formats.FormatTools;
import loci.formats.MetadataTools;
import loci.formats.meta.FilterMetadata;
import loci.formats.meta.MetadataStore;

/**
 * APLReader is the file format reader for Olympus APL files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/in/APLReader.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/in/APLReader.java">SVN</a></dd></dl>
 */
public class APLReader extends FormatReader {

  // -- Fields --

  private String[] tiffFiles;
  private String[] xmlFiles;
  private MinimalTiffReader[] tiffReaders;
  private Vector<String> used;

  // -- Constructor --

  /** Constructs a new APL reader. */
  public APLReader() {
    super("Olympus APL", new String[] {"apl", "tnb", "mtb"});
  }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getUsedFiles(boolean) */
  public String[] getUsedFiles(boolean noPixels) {
    FormatTools.assertId(currentId, true, 1);
    if (noPixels) {
      Vector<String> files = new Vector();
      for (String f : used) {
        String name = f.toLowerCase();
        if (!name.endsWith(".tif") && !name.endsWith(".tiff")) {
          files.add(f);
        }
      }
      return files.toArray(new String[0]);
    }
    return used == null ? new String[0] : used.toArray(new String[0]);
  }

  /**
   * @see loci.formats.IFormatReader#openBytes(int, byte[], int, int, int, int)
   */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return tiffReaders[series].openBytes(no, buf, x, y, w, h);
  }

  // -- IFormatHandler API methods --

  /* @see loci.formats.IFormatHandler#close() */
  public void close() throws IOException {
    super.close();
    if (tiffReaders != null) {
      for (MinimalTiffReader reader : tiffReaders) {
        reader.close();
      }
    }
    tiffReaders = null;
    tiffFiles = null;
    xmlFiles = null;
    used = null;
  }

  /* @see loci.formats.IFormatReader#fileGroupOption(String) */
  public int fileGroupOption(String id) throws FormatException, IOException {
    return FormatTools.MUST_GROUP;
  }

  // -- Internal FormatReader API methods --

  /* @see loci.formats.FormatReader#initFile(String) */
  protected void initFile(String id) throws FormatException, IOException {
    debug("APLReader.initFile(" + id + ")");
    super.initFile(id);

    // find the corresponding .mtb file
    String name = id.toLowerCase();
    if (!name.endsWith(".mtb")) {
      int separator = id.lastIndexOf(File.separator);
      if (separator < 0) separator = 0;
      int underscore = id.lastIndexOf("_");
      if (underscore < separator) underscore = id.lastIndexOf(".");
      String mtbFile = id.substring(0, underscore) + "_d.mtb";
      if (!new Location(mtbFile).exists()) {
        throw new FormatException(".mtb file not found");
      }
      currentId = new Location(mtbFile).getAbsolutePath();
    }

    String mtb = new Location(currentId).getAbsolutePath();

    Vector<String[]> rows = MDBParser.parseDatabase(mtb)[0];
    String[] columnNames = rows.get(0);

    // add full table to metadata hashtable

    for (int i=1; i<rows.size(); i++) {
      String[] row = rows.get(i);
      for (int q=0; q<row.length; q++) {
        addGlobalMeta(columnNames[q + 1] + " " + i, row[q]);
      }
    }

    used = new Vector<String>();
    used.add(id);
    if (!id.equals(mtb)) used.add(mtb);

    // calculate indexes to relevant metadata

    int calibrationUnit = calculateIndex("Calibration Unit", columnNames);
    int colorChannels = calculateIndex("Color Channels", columnNames);
    int frames = calculateIndex("Frames", columnNames);
    int calibratedHeight = calculateIndex("Height", columnNames);
    int calibratedWidth = calculateIndex("Width", columnNames);
    int path = calculateIndex("Image Path", columnNames);
    int magnification = calculateIndex("Magnification", columnNames);
    int width = calculateIndex("X-Resolution", columnNames);
    int height = calculateIndex("Y-Resolution", columnNames);
    int imageName = calculateIndex("Image Name", columnNames);

    int seriesCount = (rows.size() - 1) / 3;

    core = new CoreMetadata[seriesCount];
    for (int i=0; i<seriesCount; i++) {
      core[i] = new CoreMetadata();
    }
    tiffFiles = new String[seriesCount];
    xmlFiles = new String[seriesCount];
    tiffReaders = new MinimalTiffReader[seriesCount];

    String parentDirectory = mtb.substring(0, mtb.lastIndexOf(File.separator));

    for (int i=0; i<seriesCount; i++) {
      String[] row2 = rows.get(i * 3 + 2);
      String[] row3 = rows.get(i * 3 + 3);

      core[i].sizeZ = Integer.parseInt(row3[frames]);
      core[i].sizeT = 1;
      core[i].dimensionOrder = "XYCZT";

      xmlFiles[i] = row2[path];
      tiffFiles[i] = row3[path];

      // sanitize file names

      xmlFiles[i] = xmlFiles[i].replaceAll("/", File.separator);
      while (xmlFiles[i].indexOf('\\') != -1) {
        xmlFiles[i] = xmlFiles[i].replace('\\', File.separatorChar);
      }
      tiffFiles[i] = tiffFiles[i].replaceAll("/", File.separator);
      while (tiffFiles[i].indexOf('\\') != -1) {
        tiffFiles[i] = tiffFiles[i].replace('\\', File.separatorChar);
      }

      int slash = xmlFiles[i].lastIndexOf(File.separator);
      slash = xmlFiles[i].lastIndexOf(slash - 1);
      slash = xmlFiles[i].lastIndexOf(slash - 1);
      xmlFiles[i] =
        parentDirectory + File.separator + xmlFiles[i].substring(slash + 1);

      slash = tiffFiles[i].lastIndexOf(File.separator);
      slash = tiffFiles[i].lastIndexOf(File.separator, slash - 1);
      tiffFiles[i] =
        parentDirectory + File.separator + tiffFiles[i].substring(slash + 1);

      used.add(xmlFiles[i]);
      used.add(tiffFiles[i]);

      tiffReaders[i] = new MinimalTiffReader();
      tiffReaders[i].setId(tiffFiles[i]);

      // get core metadata from TIFF file

      core[i].sizeX = tiffReaders[i].getSizeX();
      core[i].sizeY = tiffReaders[i].getSizeY();
      core[i].sizeC = tiffReaders[i].getSizeC();
      core[i].rgb = tiffReaders[i].isRGB();
      core[i].pixelType = tiffReaders[i].getPixelType();
      core[i].littleEndian = tiffReaders[i].isLittleEndian();
      core[i].indexed = tiffReaders[i].isIndexed();
      core[i].falseColor = tiffReaders[i].isFalseColor();
      core[i].imageCount = core[i].sizeZ * (core[i].rgb ? 1 : core[i].sizeC);
    }

    MetadataStore store =
      new FilterMetadata(getMetadataStore(), isMetadataFiltered());
    MetadataTools.populatePixels(store, this);

    for (int i=0; i<seriesCount; i++) {
      String[] row3 = rows.get(i * 3 + 3);

      // populate Image data
      MetadataTools.setDefaultCreationDate(store, mtb, i);
      store.setImageName(row3[imageName], i);

      // populate Dimensions data

      // calculate physical X and Y sizes

      float realWidth = Float.parseFloat(row3[calibratedWidth]);
      float realHeight = Float.parseFloat(row3[calibratedHeight]);

      String units = row3[calibrationUnit];

      float px = realWidth / core[i].sizeX;
      float py = realHeight / core[i].sizeY;

      if (units.equals("mm")) {
        px *= 1000;
        py *= 1000;
      }
      // TODO : add cases for other units

      store.setDimensionsPhysicalSizeX(new Float(px), i, 0);
      store.setDimensionsPhysicalSizeY(new Float(py), i, 0);
    }
  }

  // -- Helper methods --

  private int calculateIndex(String key, String[] array) {
    for (int i=0; i<array.length; i++) {
      if (key.equals(array[i])) return i - 1;
    }
    return -1;
  }

}
