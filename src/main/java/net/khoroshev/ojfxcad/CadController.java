/*
 * Copyright (c) 2010, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.khoroshev.ojfxcad;

import com.javafx.experiments.exporters.fxml.FXMLExporter;
import com.javafx.experiments.exporters.javasource.JavaSourceExporter;
import com.javafx.experiments.importers.Importer3D;
import com.javafx.experiments.importers.Optimizer;
import com.javafx.experiments.jfx3dviewer.ContentModel;
import com.javafx.experiments.jfx3dviewer.Jfx3dViewerApp;
import com.javafx.experiments.jfx3dviewer.SessionManager;
import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.MeshContainer;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller class for main fxml file.
 */
public class CadController implements Initializable {
    private final ContentModel contentModel = Jfx3dViewerApp.getContentModel();
    private SessionManager sessionManager = SessionManager.getSessionManager();
    @FXML
    private VBox jfxcadPanel;
    private TextArea sourceCodeTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sourceCodeTextArea = (TextArea)jfxcadPanel.lookup("#sourceCodeTextArea");
        StringBuffer code = new StringBuffer();
        new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("bait.groovy"))).lines().forEach(s -> {
            code.append(s).append("\n");
        });
        sourceCodeTextArea.setText(code.toString());
    }


    private void setContent(Node root, Timeline timeline) {
        contentModel.setContent(root);
        contentModel.setTimeline(timeline);

        if (timeline != null) {
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        }
    }

    public void buttonExecute(ActionEvent event) {
        compile(sourceCodeTextArea.getText());
    }

    private void compile(String code) {
        CSG csgObject = null;
        try {
            CompilerConfiguration cc = new CompilerConfiguration();
            cc.addCompilationCustomizers(
                    new ImportCustomizer().
                            addStarImports("eu.mihosoft.jcsg",
                                    "eu.mihosoft.jcsj.samples",
                                    "eu.mihosoft.vvecmath")
                                    .addStaticStars("eu.mihosoft.vvecmath.Transform"));
            GroovyShell shell = new GroovyShell(Jfx3dViewerApp.class.getClassLoader(),
                    new Binding(), cc);
            Script script = shell.parse(code);
            Object obj = script.run();
            if (obj instanceof CSG) {
                CSG csg = (CSG) obj;
                csgObject = csg;
                MeshContainer meshContainer = csg.toJavaFXMesh();
                final MeshView meshView = meshContainer.getAsMeshViews().get(0);
                PhongMaterial m = new PhongMaterial(Color.RED);
                meshView.setCullFace(CullFace.NONE);
                meshView.setMaterial(m);
                setContent(meshView, null);
            } else {
                System.out.println(">> no CSG object returned :(");
            }
        } catch (Throwable ex) {
            ex.printStackTrace(System.err);
        }
    }
}
