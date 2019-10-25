package com.ksm.kakao.service;

import com.ksm.kakao.service.search.EcoProgramFinder;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public class EcoProgramFinderTest {
    @Test
    public void countMatchesTest() throws Exception {
        String str = "azb az azbb azazazbaz zbab az azb";

        Method method = EcoProgramFinder.class.getDeclaredMethod("countMatches", String.class, String.class);
        method.setAccessible(true);
        int count = (Integer) method.invoke(EcoProgramFinder.class, str, "az");

        Assert.assertEquals(9, count);
    }
}
