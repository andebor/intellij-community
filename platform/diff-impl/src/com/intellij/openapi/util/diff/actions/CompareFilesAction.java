/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.openapi.util.diff.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.diff.impl.DiffRequestFactory;
import com.intellij.openapi.util.diff.requests.DiffRequest;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompareFilesAction extends BaseShowDiffAction {
  public static final DataKey<DiffRequest> DIFF_REQUEST = DataKey.create("CompareFilesAction.DiffRequest");

  protected boolean isAvailable(@NotNull AnActionEvent e) {
    DiffRequest request = e.getData(DIFF_REQUEST);
    if (request != null) {
      return true;
    }

    VirtualFile[] files = e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
    if (files == null) {
      return false;
    }

    if (files.length == 1) {
      return files[0].isValid();
    }
    else if (files.length == 2) {
      return files[0].isValid() && files[1].isValid();
    }
    else {
      return false;
    }
  }

  @Nullable
  @Override
  protected DiffRequest getDiffRequest(@NotNull AnActionEvent e) {
    Project project = e.getProject();
    DiffRequest diffRequest = e.getData(DIFF_REQUEST);
    if (diffRequest != null) {
      return diffRequest;
    }

    VirtualFile[] data = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
    if (data.length == 1) {
      // TODO: filter depending on type of file: file, directory, archieve ?
      FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, true, false);
      VirtualFile[] result = FileChooser.chooseFiles(descriptor, project, data[0]);
      if (result.length != 1 || result[0] == null) return null;

      return DiffRequestFactory.createFromFile(project, data[0], result[0]);
    }
    else {
      return DiffRequestFactory.createFromFile(project, data[0], data[1]);
    }
  }
}
