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
package edu.utep.cybershare.wdoit.ui.components;

import java.util.Vector;

/**
 * Combo box that shows the choices of agents. Populates list from a specified
 * web service source of agents. Maintains cache of agents for when working
 * off-line.
 * 
 * @author Leonardo Salayandia
 */
public class AgentComboBox extends IndividualComboBox {
	private static final long serialVersionUID = 1L;

	public AgentComboBox() {
		super();
		queryAgents();
	}

	public void queryAgents() {
		Vector<Individual> individuals = new Vector<Individual>();
		individuals.add(new Individual(
				"http://trust.utep.edu/agents#leonardo_salayandia",
				"Leonardo Salayandia",
				"Research Specialist at CyberShARE Center"));
		individuals.add(new Individual(
				"http://trust.utep.edu/agents#paulo_pinheiro_dasilva",
				"Paulo Pinheiro da Silva",
				"Principal Investigator at CyberShARE Center"));
		individuals.add(new Individual(
				"http://trust.utep.edu/agents#aida_gandara", "Aida Gandara",
				"Computer Science Doctoral Student at Univ Texas at El Paso"));
		individuals
				.add(new Individual(
						"http://trust.utep.edu/agents#nicholas_delrio",
						"Nicholas Del Rio",
						"Computer Science Doctoral Student by day, Hollywood stunt man by night"));
		individuals.add(new Individual(
				"http://trust.utep.edu/agents#jitin_arora", "Jitin Arora",
				"Triple Store Master of the Universe"));

		// try web service and update local store

		// if web service not reached, populate from local store

		this.setIndividuals(individuals);
	}
}