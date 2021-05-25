package com.airsaid.localization.task;

import org.junit.Test;

import static org.junit.Assert.*;


public class EscapeUtilTest {

    @Test
    public void removeEscapeSequences() {

        assertEquals("It's \"-quote @, or not?",
                EscapeUtil.removeEscapeSequences("It\\'s \\\"-quote \\@, or not\\?"));
        assertEquals("It's < than & \"home\"",
                EscapeUtil.removeEscapeSequences("It&apos;s &lt; than &amp; &quot;home&quot;"));


    }

    @Test
    public void addEscapeSequences() {
        assertEquals("It\\'s \\\"-quote \\@, or not\\?",
                EscapeUtil.addEscapeSequences("It's \"-quote @, or not?"));
        assertEquals("It\\'s &lt; than &amp; \\\"home\\\"",
                EscapeUtil.addEscapeSequences("It's < than & \"home\""));

    }
}