/** 
 * Copyright (c) 2007-2008, Regents of the University of Colorado 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer. 
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * Neither the name of the University of Colorado at Boulder nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. 
 */
package org.cleartk.classifier;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.cleartk.CleartkException;
import org.cleartk.classifier.encoder.EncoderFactory;
import org.cleartk.classifier.encoder.factory.NameNumberEncoderFactory;
import org.cleartk.classifier.encoder.features.FeaturesEncoder;
import org.cleartk.classifier.encoder.features.FeaturesEncoder_ImplBase;
import org.cleartk.classifier.encoder.features.NameNumber;
import org.cleartk.classifier.encoder.outcome.OutcomeEncoder;
import org.cleartk.classifier.mallet.MalletDataWriter;
import org.cleartk.classifier.opennlp.MaxentClassifierBuilder;
import org.cleartk.classifier.opennlp.MaxentDataWriter;
import org.cleartk.util.UIMAUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uutuc.factory.AnalysisEngineFactory;
import org.uutuc.factory.UimaContextFactory;
import org.uutuc.util.TearDownUtil;

/**
 * <br>
 * Copyright (c) 2007-2008, Regents of the University of Colorado <br>
 * All rights reserved.
 * 
 * 
 * @author Steven Bethard
 */
public class DataWriter_ImplBaseTests {

	@After
	public void tearDown() throws Exception {
		TearDownUtil.removeDirectory(outputDirectory);
	}

	@Test
	public void testManifest() throws UIMAException, IOException, CleartkException {
		String expectedManifest = ("Manifest-Version: 1.0\n"
				+ "classifierBuilderClass: org.cleartk.classifier.opennlp.MaxentClassifie\n" + " rBuilder");

		DataWriter_ImplBase<String, String, List<NameNumber>> dataWriter = new MaxentDataWriter(outputDirectory);
		dataWriter.finish();
		File manifestFile = new File(outputDirectory, "MANIFEST.MF");
		String actualManifest = FileUtils.file2String(manifestFile);
		Assert.assertEquals(expectedManifest, actualManifest.replaceAll("\r", "").trim());
	}

	@Test
	public void testPrintWriter() throws UIMAException, IOException, CleartkException {

		DataWriter_ImplBase<String, String, List<NameNumber>> dataWriter = new MaxentDataWriter(outputDirectory);
		PrintWriter printWriter = dataWriter.getPrintWriter("foo.txt");
		printWriter.println("foo");
		dataWriter.finish();
		String actualText = FileUtils.file2String(new File(outputDirectory, "foo.txt"));
		Assert.assertEquals("foo\n", actualText.replaceAll("\r", ""));

		try {
			printWriter = dataWriter.getPrintWriter(".");
			Assert.fail("expected exception on bad file name");
		}
		catch (IOException ioe) { }
	}

	@Test
	public void testFinish() throws UIMAException, IOException, CleartkException {

		DataWriter_ImplBase<String, String, List<NameNumber>> dataWriter = new MalletDataWriter(outputDirectory);
		NameNumberEncoderFactory nnef = new NameNumberEncoderFactory();
		UimaContext uimaContext = UimaContextFactory.createUimaContext();
		dataWriter.setFeaturesEncoder((FeaturesEncoder<List<NameNumber>>)(nnef.createFeaturesEncoder(uimaContext)));
		dataWriter.setOutcomeEncoder((OutcomeEncoder<String, String>)(nnef.createOutcomeEncoder(uimaContext)));
		dataWriter.finish();
		assertTrue(new File(outputDirectory, FeaturesEncoder_ImplBase.ENCODERS_FILE_NAME).exists());
		
	}


	private final String outputDirectoryName = "test/data/classifiers"; 
	private final File outputDirectory = new File(outputDirectoryName);



}
