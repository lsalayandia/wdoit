/*
Copyright (c) 2012, University of Texas at El Paso
All rights reserved.
Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:
- Redistributions of source code must retain the above copyright notice, this 
  list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright notice, 
  this list of conditions and the following disclaimer in the documentation 
  and/or other materials provided with the distribution.
  
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
  ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
  LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
  SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
  POSSIBILITY OF SUCH DAMAGE.
 */
package edu.utep.cybershare.wdoit.tree;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.hp.hpl.jena.ontology.OntClass;

import edu.utep.cybershare.wdoit.graphics.OntClassNode;

/**
 * @author Leonardo Salayandia
 * 
 */
public class OntClassTransferable implements Transferable {

	// flavors supported
	public static final DataFlavor ONTCLASS_FLAVOR = new DataFlavor(
			OntClass.class, "OntClassFlavor");
	public static final DataFlavor RELATION_NODE_FLAVOR = new DataFlavor(
			OntClassNode.class, "RelationNodeFlavor");

	// flavors list
	private static final DataFlavor[] flavors = { ONTCLASS_FLAVOR,
			RELATION_NODE_FLAVOR };
	private static final List<DataFlavor> flavorList = Arrays.asList(flavors);

	// concept to transfer
	private OntClass ontclass;

	/**
	 * 
	 * @param ontclass
	 */
	public OntClassTransferable(OntClass ontclass) {
		super();
		this.ontclass = ontclass;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer
	 * .DataFlavor)
	 */
	public synchronized Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (flavor.equals(ONTCLASS_FLAVOR))
			return ontclass;
		else if (flavor.equals(RELATION_NODE_FLAVOR))
			return (new OntClassNode(ontclass, new Point(0, 0), false));
		else
			throw new UnsupportedFlavorException(flavor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		return flavors;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.
	 * datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return (flavorList.contains(flavor));
	}
}
