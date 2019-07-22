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
