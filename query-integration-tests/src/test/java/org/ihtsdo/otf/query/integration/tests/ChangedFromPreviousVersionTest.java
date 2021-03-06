package org.ihtsdo.otf.query.integration.tests;

/*
 * Copyright 2013 International Health Terminology Standards Development Organisation.
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
import java.io.IOException;
import org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetBI;
import org.ihtsdo.otf.query.implementation.Clause;
import org.ihtsdo.otf.query.implementation.Query;
import org.ihtsdo.otf.tcc.api.nid.ConcurrentBitSet;
import org.ihtsdo.otf.tcc.api.nid.NativeIdSetItrBI;
import org.ihtsdo.otf.tcc.api.store.Ts;

/**
 * Creates a test for <code>ChangedFromPreviousVersion</code> clause.
 *
 * @author dylangrald
 */
public class ChangedFromPreviousVersionTest extends QueryClauseTest {

    NativeIdSetBI forSet;

    public ChangedFromPreviousVersionTest() throws IOException {
        this.q = new Query() {
            @Override
            protected NativeIdSetBI For() throws IOException {
                forSet = new ConcurrentBitSet();
                forSet.or(Ts.get().isChildOfSet(Snomed.CLINICAL_FINDING.getNid(), StandardViewCoordinates.getSnomedInferredLatest()));
                forSet.add(Snomed.CLINICAL_FINDING.getNid());
                return forSet;
            }

            @Override
            public void Let() throws IOException {
                SetViewCoordinate setViewCoordinate = new SetViewCoordinate(2010, 1, 31, 0, 0);
                let("v2", setViewCoordinate.getViewCoordinate());
            }

            @Override
            public Clause Where() {
                return Or(ChangedFromPreviousVersion("v2"));
            }
        };

    }
}
