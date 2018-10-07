package com.ExecutablesDBpedia;

import com.dbpediaSentenseWindow.EntityToSW;

public final class EntitiesToES {

    private EntitiesToES() {
    }

    public static void main(String[] args) {
        EntityToSW entityToSW = new EntityToSW();
        entityToSW.entitiesToESWorkflow(50, 200);
    }


}