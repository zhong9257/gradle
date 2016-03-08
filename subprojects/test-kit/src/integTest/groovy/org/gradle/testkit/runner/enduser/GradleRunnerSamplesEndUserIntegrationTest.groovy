/*
 * Copyright 2016 the original author or authors.
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


package org.gradle.testkit.runner.enduser

import org.gradle.integtests.fixtures.Sample
import org.gradle.integtests.fixtures.UsesSample
import org.gradle.testkit.runner.fixtures.NoDebug
import org.gradle.testkit.runner.fixtures.NonCrossVersion
import org.gradle.util.Requires
import org.gradle.util.TestPrecondition
import org.junit.Rule

@NonCrossVersion
@NoDebug
class GradleRunnerSamplesEndUserIntegrationTest extends BaseTestKitEndUserIntegrationTest {

    @Rule
    Sample sample = new Sample(testDirectoryProvider)

    @UsesSample("testKit/testKitJunit")
    def junit() {
        expect:
        executer.inDirectory(sample.dir)
        succeeds "check"
    }

    @UsesSample("testKit/testKitSpock")
    def spock() {
        expect:
        executer.inDirectory(sample.dir)
        succeeds "check"
    }

    @UsesSample("testKit/testKitSpockClasspath")
    def buildscriptClasspath() {
        expect:
        executer.inDirectory(sample.dir)
        succeeds "check"
    }

    @UsesSample("testKit/testKitSpockAutomaticClasspath/quickstart")
    def automaticClasspath() {
        expect:
        executer.inDirectory(sample.dir)
        succeeds "check"
    }

    @UsesSample("testKit/testKitSpockAutomaticClasspath/customTestSourceSet")
    def customTestSourceSet() {
        expect:
        executer.inDirectory(sample.dir)
        succeeds "check"
    }

    @Requires([TestPrecondition.ONLINE, TestPrecondition.JDK8_OR_EARLIER])
    @UsesSample("testKit/testKitSpockGradleVersion")
    def version() {
        expect:
        executer.inDirectory(sample.dir)
        succeeds "check"
    }
}