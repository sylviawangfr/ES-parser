package com.Executables;

import com.esutil.OWL2NT;
import com.esutil.SentenseSpliter;
import com.esutil.ToOpenKE;
import com.htmlSentenceWindow.ESEngineSW;
import com.htmlSentenceWindow.ResultHitJsonSW;
import com.htmlSentenceWindowWithMeta.ESEngineSWM;
import com.htmlSentenceWindowWithMeta.ResultHitJsonSWM;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SandBox {

    private SandBox() {
    }

    public static void main(String[] args) {

        ESEngineSWM esEngine = new ESEngineSWM();
        esEngine.deleteTmpResult();
//        OWL2NT converter = new OWL2NT();
//        Map<String, Integer> labels = converter.getAllLabels();
//        Set<String> keys = labels.keySet();
//        for (String l : keys) {
//            System.out.println(l + " " + labels.get(l) + "\n");
//        }

//        ToOpenKE toOpenKE = new ToOpenKE();
//        toOpenKE.convert(converter.parseEntitiesObjectOnly());

    }


}