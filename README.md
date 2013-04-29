Faster Intraprocedural Analysis of McLab
============

Overview
--------
This project aimed to improve McLab's static analysis framework i.e. McSAF. The detailed project report is added in the repository by the name of COMP_621_Project_Report.pdf
The implementation, results and discussion are discussed in the project report.


My contribution to the mclab code
---------------------
To facilitate the enduser with configuration, I have added complete mclab code in the repository. My contribution in the code is explained below:
Package: mclab.languages.Natlab.src.analysis
	1. ImprovedLiveVariableAnalysis.java : I wrote this file afresh and it implements live variable analysis using BitVector datastructure.
	2. SerializeFlowVariables.java : I wrote this file as a part of serializing inFlowSets and outFlowSets. I no more use this file as this implementation produced negative results
	3. Main.java : This file runs the new implementation and old implemetation for comparision of analysis times.
	4. LiveVariableAnalysis.java : This file performs Live variable analysis using HashSetFlowSet datastructure.
	5. BitVectorFlowVector.java : This file is being as datastructure in ImprovedLiveVariableAnalysis. It extends BitSet class of java and provides a copy function for cloning an object.
	
Package: mclab.languages.Natlab.gen.ast
	1. ASTNode.java	: In this file, I added fields to saves the gen and kill set for each node in the tree. 

Package: mclab.languages.Natlab
	This package contains all the benchmarks that were used in the project for testing purposes.

Disclaimer
----------


Copyright and License
---------------------
Copyright 2008-2013 Amina Aslam, Toheed Aslam, Ismail Badawi, Andrew Bodzay,
Andrew Casey, Maxime Chevalier-Boisvert, Jesse Doherty, Anton Dubrau,
Rahul Garg, Vineet Kumar, Nurudeen Lameed, Jun Li, Xu Li, Soroush Radpour,
Olivier Savary Belanger, Laurie Hendren, Clark Verbrugge and McGill
University.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this work except in compliance with the License. You may obtain a copy
of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the LICENSE.
