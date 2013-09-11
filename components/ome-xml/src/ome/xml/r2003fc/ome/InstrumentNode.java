/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via xsd-fu on 2008-10-16 06:18:35-0500
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.r2003fc.ome;

import ome.xml.DOMUtil;
import ome.xml.OMEXMLNode;
import ome.xml.r2003fc.ome.*;

import org.w3c.dom.Element;

/**
 * @deprecated The r2003fc classes are replaced by the ome.xml.model classes
 */
@Deprecated
public class InstrumentNode extends OMEXMLNode
{

	// -- Constructors --

	/** Constructs a Instrument node with an associated DOM element. */
	public InstrumentNode(Element element)
	{
		super(element);
	}

	/**
	 * Constructs a Instrument node with an associated DOM element beneath
	 * a given parent.
	 */
	public InstrumentNode(OMEXMLNode parent)
	{
		this(parent, true);
	}

	/**
	 * Constructs a Instrument node with an associated DOM element beneath
	 * a given parent.
	 */
	public InstrumentNode(OMEXMLNode parent, boolean attach)
	{
		super(DOMUtil.createChild(parent.getDOMElement(),
		                          "Instrument", attach));
	}

	// -- Instrument API methods --

	// Element which occurs more than once
	public int getLightSourceCount()
	{
		return getChildCount("LightSource");
	}

	public java.util.Vector getLightSourceList()
	{
		return getChildNodes("LightSource");
	}

	public LightSourceNode getLightSource(int index)
	{
		return (LightSourceNode) getChildNode("LightSource", index);
	}

	// Virtual, inferred back reference Image_BackReference
	public int getReferringImageCount()
	{
		return getReferringCount("Image");
	}

	public java.util.List getReferringImageList()
	{
		return getReferringNodes("Image");
	}

	// Element which occurs more than once
	public int getOTFCount()
	{
		return getChildCount("OTF");
	}

	public java.util.Vector getOTFList()
	{
		return getChildNodes("OTF");
	}

	public OTFNode getOTF(int index)
	{
		return (OTFNode) getChildNode("OTF", index);
	}

	// Element which occurs more than once
	public int getFilterCount()
	{
		return getChildCount("Filter");
	}

	public java.util.Vector getFilterList()
	{
		return getChildNodes("Filter");
	}

	public FilterNode getFilter(int index)
	{
		return (FilterNode) getChildNode("Filter", index);
	}

	// Element which is complex (has sub-elements)
	public MicroscopeNode getMicroscope()
	{
		return (MicroscopeNode)
			getChildNode("Microscope", "Microscope");
	}

	// Element which occurs more than once
	public int getObjectiveCount()
	{
		return getChildCount("Objective");
	}

	public java.util.Vector getObjectiveList()
	{
		return getChildNodes("Objective");
	}

	public ObjectiveNode getObjective(int index)
	{
		return (ObjectiveNode) getChildNode("Objective", index);
	}

	// Element which occurs more than once
	public int getDetectorCount()
	{
		return getChildCount("Detector");
	}

	public java.util.Vector getDetectorList()
	{
		return getChildNodes("Detector");
	}

	public DetectorNode getDetector(int index)
	{
		return (DetectorNode) getChildNode("Detector", index);
	}

	// *** WARNING *** Unhandled or skipped property ID

	// -- OMEXMLNode API methods --

	public boolean hasID()
	{
		return true;
	}

}