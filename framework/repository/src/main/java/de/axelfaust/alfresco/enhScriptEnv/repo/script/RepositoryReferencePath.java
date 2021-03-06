/*
 * Copyright 2016 Axel Faust
 *
 * Licensed under the Eclipse Public License (EPL), Version 1.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at
 *
 * https://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package de.axelfaust.alfresco.enhScriptEnv.repo.script;

import de.axelfaust.alfresco.enhScriptEnv.common.script.ReferenceScript.ReferencePathType;

/**
 * @author Axel Faust
 */
public enum RepositoryReferencePath implements ReferencePathType
{

    NODE_REF, CONTENT_PROPERTY, FILE_FOLDER_PATH;
    // TODO Need to register URL handler to resolve Repository stored scripts to source (for Rhino debugger Dim.loadSource())

}
