/*
 *  Copyright 2019 Anthem Engineering LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.anthemengineering.sox.forkedprocess;

import com.anthemengineering.sox.utils.SoxException;

import java.util.List;

public class SoxProcessException extends SoxException {
    private final String commandLine;

    public SoxProcessException(String message, List<String> commandLine) {
        super(message);
        this.commandLine = buildCli(commandLine);
    }

    public SoxProcessException(int exitValue, String errorString, List<String> commandLine) {
        this(String.format("Sox exited with exit code %d: %s", exitValue, errorString), commandLine);
    }

    public SoxProcessException(String message, List<String> commandLine, Throwable cause) {
        super(message, cause);
        this.commandLine = buildCli(commandLine);
    }

    public SoxProcessException(SoxProcessException e) {
        super(e.getMessage(), e.getCause());
        this.commandLine = e.commandLine;
    }

    public String getCommandLine() {
        return commandLine;
    }

    private static String buildCli(List<String> commandLine) {
        StringBuilder cliBuilder = new StringBuilder();
        for (String arg : commandLine) {
            cliBuilder.append("\"").append(arg).append("\" ");
        }

        return cliBuilder.substring(0, cliBuilder.length() - 1);
    }
}
