rem Copyright (c) 2012, University of Texas at El Paso
rem All rights reserved.
rem Redistribution and use in source and binary forms, with or without 
rem modification, are permitted provided that the following conditions are met:
rem - Redistributions of source code must retain the above copyright notice, this 
rem   list of conditions and the following disclaimer.
rem - Redistributions in binary form must reproduce the above copyright notice, 
rem   this list of conditions and the following disclaimer in the documentation 
rem   and/or other materials provided with the distribution.
rem   
rem   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
rem   AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
rem   IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
rem   ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
rem   LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
rem   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
rem   SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
rem   INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
rem   CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
rem   ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
rem   POSSIBILITY OF SUCH DAMAGE.

@echo off
cls
echo WDO-It! is now loading...
rem java -Djava.library.path="lib" -cp wdo.jar;lib\activation-1.0.2.jar;lib\appframework-1.0.3.jar;lib\arq-2.8.3.jar;lib\aterm-java-1.6.jar;lib\axis-1.4.jar;lib\axis-wsdl4j-1.5.1.jar;lib\ciclient.jar;lib\commons-codec-1.4.jar;lib\commons-discovery-0.2.jar;lib\dataAnnotator.jar;lib\icu4j-3.4.4.jar;lib\iri-0.7.jar;lib\jaxrpc-api-1.1.jar;lib\jcl-over-slf4j-1.5.8.jar;lib\jena-2.6.2.jar;lib\jgraph.jar;lib\jgrapht-jdk1.5-0.7.3.jar;lib\junit-4.5.jar;lib\log4j-1.2.14.jar;lib\lucene-core-2.3.1.jar;lib\pellet-2.0.jar;lib\pml-lightweight.jar;lib\relaxngDatatype-20020414.jar;lib\saaj-api-1.2.jar;lib\slf4j-api-1.5.8.jar;lib\slf4j-log4j12-1.5.8.jar;lib\stax-api-1.0.1.jar;lib\swing-worker-1.1.jar;lib\upnp-1.0.jar;lib\wstx-asl-3.2.9.jar;lib\ws-commons-util-1.0.2.jar;lib\xercesImpl-2.7.1.jar;lib\xmlrpc-client-3.1.2.jar;lib\xmlrpc-common-3.1.2.jar;lib\xsdlib-20030225.jar edu.utep.cybershare.wdoit.WdoApp %1 %2
java -Djava.library.path="lib" -cp wdo.jar;lib\activation-1.0.2.jar;lib\appframework-1.0.3.jar;lib\arq-2.8.3.jar;lib\aterm-java-1.6.jar;lib\axis-1.4.jar;lib\axis-wsdl4j-1.5.1.jar;lib\commons-codec-1.4.jar;lib\commons-discovery-0.2.jar;lib\commons-httpclient-3.1_2.jar;lib\dataAnnotator.jar;lib\icu4j-3.4.4.jar;lib\iri-0.7.jar;lib\jaxrpc-api-1.1.jar;lib\jcl-over-slf4j-1.5.8.jar;lib\jena-2.6.2.jar;lib\jgraph.jar;lib\jgrapht-jdk1.5-0.7.3.jar;lib\junit-4.5.jar;lib\log4j-1.2.14.jar;lib\lucene-core-2.3.1.jar;lib\pellet-2.0.jar;lib\pml-lightweight.jar;lib\relaxngDatatype-20020414.jar;lib\saaj-api-1.2.jar;lib\slf4j-api-1.5.8.jar;lib\slf4j-log4j12-1.5.8.jar;lib\stax-api-1.0.1.jar;lib\swing-worker-1.1.jar;lib\upnp-1.0.jar;lib\wstx-asl-3.2.9.jar;lib\ws-commons-util-1.0.2.jar;lib\xercesImpl-2.7.1.jar;lib\xmlrpc-client-3.1.2.jar;lib\xmlrpc-common-3.1.2.jar;lib\xsdlib-20030225.jar edu.utep.cybershare.wdoit.WdoApp %1 %2
