/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lspb.flatlaftest;

import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.*;



/**
 *
 * @author santiago
 */
public class FlatlafTest {

    public static void main(String[] args) {
        FlatMTAtomOneDarkIJTheme.setup();
        FramePrincipal a=new FramePrincipal();
        a.setVisible(true);
    }
}
