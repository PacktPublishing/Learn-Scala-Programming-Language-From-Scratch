



package com.eduonix;
import com.gbeans.GTemplate;
import com.sbeans.ScalaClass;

public class MainRunner {

    public static void main(String[] args) {

        // run groovy in the jvm
        GTemplate grunner = new GTemplate();
        grunner.multiply(4,3);
        System.out.println("Groovy  runs "
                +grunner.multiply(4,3));

        // run scala in the jvm
        ScalaClass sc = new ScalaClass();
        System.out.println("Scala  runs "
                +sc.multiply(4,3));
    }
}
