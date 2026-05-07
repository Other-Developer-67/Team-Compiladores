/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lspb.randomizar.numeros;

/**
 *
 * @author santiago
 */
public class RandomizarNumeros {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        for (int i = 0; i < 10; i++) {
            int numero = (int) (Math.random() * 101); //RANDOMIZA UN NUMERO ENTRE 0 Y 100
            System.out.print(numero + " ");
        }
        System.out.println("");
        int numero = (int) (Math.random() * 100)+1; //RANDOMIZA UN NUMERO ENTRE 1 Y 100
        System.out.println("Numero Elegido: "+numero);
    }
}
