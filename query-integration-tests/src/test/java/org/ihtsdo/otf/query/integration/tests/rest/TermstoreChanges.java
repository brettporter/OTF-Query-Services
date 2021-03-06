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
package org.ihtsdo.otf.query.integration.tests.rest;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ihtsdo.otf.query.integration.tests.QueryTest;
import org.ihtsdo.otf.tcc.api.blueprint.ComponentProperty;
import org.ihtsdo.otf.tcc.api.blueprint.DescriptionCAB;
import org.ihtsdo.otf.tcc.api.blueprint.IdDirective;
import org.ihtsdo.otf.tcc.api.blueprint.InvalidCAB;
import org.ihtsdo.otf.tcc.api.blueprint.RefexCAB;
import org.ihtsdo.otf.tcc.api.blueprint.RefexDirective;
import org.ihtsdo.otf.tcc.api.blueprint.TerminologyBuilderBI;
import org.ihtsdo.otf.tcc.api.contradiction.ContradictionException;
import org.ihtsdo.otf.tcc.api.coordinate.EditCoordinate;
import org.ihtsdo.otf.tcc.api.coordinate.Status;
import org.ihtsdo.otf.tcc.api.coordinate.ViewCoordinate;
import org.ihtsdo.otf.tcc.api.description.DescriptionChronicleBI;
import org.ihtsdo.otf.tcc.api.description.DescriptionVersionBI;
import org.ihtsdo.otf.tcc.api.metadata.binding.Snomed;
import org.ihtsdo.otf.tcc.api.metadata.binding.TermAux;
import org.ihtsdo.otf.tcc.api.refex.RefexChronicleBI;
import org.ihtsdo.otf.tcc.api.refex.RefexType;
import org.ihtsdo.otf.tcc.api.store.Ts;

/**
 *
 * @author dylangrald
 */
public class TermstoreChanges {

    public ViewCoordinate vc;
    
    public TermstoreChanges(ViewCoordinate vc) {
        this.vc = vc;
    }

    public void setActiveStatus(DescriptionVersionBI desc, Status status) throws IOException, ContradictionException, InvalidCAB {
        DescriptionCAB descCAB = desc.makeBlueprint(vc, IdDirective.PRESERVE, RefexDirective.EXCLUDE);
        descCAB.setStatus(status);
        int authorNid = TermAux.USER.getLenient().getConceptNid();
        int editPathNid = TermAux.SNOMED_CORE.getLenient().getConceptNid();
        EditCoordinate ec = new EditCoordinate(authorNid, Snomed.CORE_MODULE.getLenient().getNid(), editPathNid);
        TerminologyBuilderBI tb = Ts.get().getTerminologyBuilder(ec, vc);
        DescriptionChronicleBI descChronicle = tb.construct(descCAB);
        Ts.get().addUncommitted(desc.getEnclosingConcept().getVersion(vc));
        Ts.get().commit();
        System.out.println(descChronicle.getVersion(vc));
    }

    public void addRefsetMember() throws IOException {
        try {
            RefexCAB refex = new RefexCAB(RefexType.STR, Snomed.MILD.getLenient().getNid(), Snomed.SEVERITY_REFSET.getLenient().getNid(), IdDirective.GENERATE_HASH, RefexDirective.INCLUDE);
            refex.put(ComponentProperty.STRING_EXTENSION_1, "Mild severity");
            int authorNid = TermAux.USER.getLenient().getConceptNid();
            int editPathNid = TermAux.WB_AUX_PATH.getLenient().getConceptNid();
            EditCoordinate ec = new EditCoordinate(authorNid, Snomed.CORE_MODULE.getLenient().getNid(), editPathNid);
            TerminologyBuilderBI tb = Ts.get().getTerminologyBuilder(ec, org.ihtsdo.otf.tcc.api.coordinate.StandardViewCoordinates.getSnomedInferredLatest());
            RefexChronicleBI rc = tb.construct(refex);
            Ts.get().addUncommitted(Snomed.SEVERITY_REFSET.getLenient());
            Ts.get().commit();

        } catch (InvalidCAB ex) {
            Logger.getLogger(QueryTest.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (ContradictionException ex) {
            Logger.getLogger(QueryTest.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void modifyDesc(String text, int nid) throws IOException, ContradictionException, InvalidCAB {
        DescriptionVersionBI desc = Ts.get().getConceptVersion(vc, nid).getPreferredDescription();
        DescriptionCAB descCAB = desc.makeBlueprint(vc, IdDirective.PRESERVE, RefexDirective.EXCLUDE);
        descCAB.setText(text);
        int authorNid = TermAux.USER.getLenient().getConceptNid();
        int editPathNid = TermAux.SNOMED_CORE.getLenient().getConceptNid();
        EditCoordinate ec = new EditCoordinate(authorNid, Snomed.CORE_MODULE.getLenient().getNid(), editPathNid);
        TerminologyBuilderBI tb = Ts.get().getTerminologyBuilder(ec, vc);
        DescriptionChronicleBI descChronicle = tb.construct(descCAB);
        Ts.get().addUncommitted(desc.getEnclosingConcept().getVersion(vc));
        Ts.get().commit();
    }

}
