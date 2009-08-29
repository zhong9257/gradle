/*
 * Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.configuration;

import org.gradle.api.internal.GradleInternal;
import org.gradle.api.internal.artifacts.dsl.BuildScriptClasspathScriptTransformer;
import org.gradle.api.internal.artifacts.dsl.BuildScriptTransformer;
import org.gradle.api.internal.initialization.ScriptClassLoaderProvider;
import org.gradle.api.internal.project.ImportsReader;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.internal.project.ProjectScript;
import org.gradle.groovy.scripts.*;
import static org.gradle.util.Matchers.*;
import static org.hamcrest.Matchers.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(JMock.class)
public class BuildScriptCompilerTest {
    private final JUnit4Mockery context = new JUnit4Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};
    private final ProjectInternal project = context.mock(ProjectInternal.class);
    private final GradleInternal gradle = context.mock(GradleInternal.class);
    private final ScriptSource scriptSource = context.mock(ScriptSource.class);
    private final ScriptProcessorFactory scriptProcessorFactory = context.mock(ScriptProcessorFactory.class);
    private final ScriptProcessor processor = context.mock(ScriptProcessor.class);
    private final ImportsReader importsReader = context.mock(ImportsReader.class);
    private final ClassLoader classLoader = context.mock(ClassLoader.class);
    private final ScriptRunner classpathScriptRunner = context.mock(ScriptRunner.class, "classpath");
    private final ScriptRunner buildScriptRunner = context.mock(ScriptRunner.class, "build");
    private final ScriptClassLoaderProvider classLoaderProvider = context.mock(ScriptClassLoaderProvider.class);
    private final File rootDir = new File("root dir");
    private final Script buildScript = context.mock(Script.class);
    private final BuildScriptCompiler evaluator = new BuildScriptCompiler(importsReader, scriptProcessorFactory);

    @Before
    public void setUp() {
        context.checking(new Expectations() {{
            allowing(project).getGradle();
            will(returnValue(gradle));

            allowing(project).getBuildScriptSource();
            will(returnValue(scriptSource));

            allowing(project).getRootDir();
            will(returnValue(rootDir));

            allowing(project).getClassLoaderProvider();
            will(returnValue(classLoaderProvider));
        }});
    }

    @Test
    public void createsAndExecutesScriptAndNotifiesListener() {
        final ScriptSource expectedScriptSource = new ImportsScriptSource(scriptSource, importsReader, rootDir);

        context.checking(new Expectations() {{
            one(scriptProcessorFactory).createProcessor(with(reflectionEquals(expectedScriptSource)));
            will(returnValue(processor));

            one(classLoaderProvider).getClassLoader();
            will(returnValue(classLoader));
            
            one(processor).setClassloader(classLoader);

            one(processor).setTransformer(with(notNullValue(BuildScriptClasspathScriptTransformer.class)));

            one(processor).compile(ProjectScript.class);
            will(returnValue(classpathScriptRunner));

            one(classpathScriptRunner).setDelegate(project);

            one(classpathScriptRunner).run();

            one(classLoaderProvider).updateClassPath();
            
            one(processor).setTransformer(with(notNullValue(BuildScriptTransformer.class)));

            one(processor).compile(ProjectScript.class);
            will(returnValue(buildScriptRunner));

            one(buildScriptRunner).setDelegate(project);

            one(buildScriptRunner).getScript();
            will(returnValue(buildScript));

            one(project).setScript(buildScript);

            one(buildScriptRunner).run();
        }});

        evaluator.evaluate(project);
    }
}