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

package com.anthemengineering.sox.apiTest;

import com.anthemengineering.sox.SoxException;
import com.anthemengineering.sox.effects.utils.OptList;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class OptListTest {
    @Test
    public void orderedArgumentsMustNotHaveEmptyMiddleValues() {
        OptList optList = new OptList();
        try {
            optList.addOrderArguments(new String[]{"a", "b", "c"}, "aaa", null, "ccc");

            fail("Should throw an exception.");
        } catch (SoxException e) {
            assertThat(e).hasMessage("Option 'c' requires the previous option 'b' to be set.");
        }
    }

    @Test
    public void orderedArgumentsShouldAllowAllEmptyAndNulls() {
        OptList optList = new OptList();
        optList.addOrderArguments(new String[]{"a", "b", "c"}, "", null, " ");
        assertThat(optList.toStringArray()).isEmpty();
    }

    @Test
    public void orderedArgumentsShouldAllowNoRegisteredArguments() {
        OptList optList = new OptList();
        optList.addOrderArguments(new String[0]);
        assertThat(optList.toStringArray()).isEmpty();
    }

    @Test
    public void orderedArgumentsShouldAllowPartialArgumentList() {
        OptList optList = new OptList();
        optList.addOrderArguments(new String[]{"a", "b", "c"}, "aaa", null, " ");
        assertThat(optList.toStringArray()).containsExactly("aaa");
    }

    @Test
    public void orderedArgumentsShouldAllowFullSpecification() {
        OptList optList = new OptList();
        optList.addOrderArguments(new String[]{"a", "b", "c"}, "aaa", "bb", "  ccc  ");
        assertThat(optList.toStringArray()).containsExactly("aaa", "bb", "ccc");
    }

    @Test
    public void addShouldSkipIfEmptyOrNull() {
        OptList optList = new OptList();
        optList.add("");
        optList.add(null);
        optList.add("    ");

        assertThat(optList.toStringArray()).isEmpty();
    }

    @Test
    public void addShouldOnlyAddActualValues() {
        OptList optList = new OptList();
        optList.add("aaa");
        optList.add("");
        optList.add(null);
        optList.add("    ");
        optList.add("bbb");

        assertThat(optList.toStringArray()).containsExactly("aaa", "bbb");
    }
}
