/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.lspb.ejemplo_integracion_ia;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

/**
 *
 * @author santiago
 */
public class Ejemplo_Integracion_IA {

    public static void main(String[] args) {
        // Configurar cliente apuntando a DeepSeek
        OpenAIClient client = OpenAIOkHttpClient.builder()
            .baseUrl("https://api.deepseek.com/v1")
            .apiKey("Clave de Deepseek") //Clave de DeepSeek
            .build();
        
        // Los parámetros son iguales que con OpenAI
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
            .addUserMessage("Cuentame las noticias mas relevantes que han ocurrido este 2026")
            .model("deepseek-chat") //Modelo de DeepSeek
            .build();
        
        ChatCompletion completion = client.chat().completions().create(params);
        String respuesta = completion.choices().get(0).message().content().orElse("...");
        
        System.out.println("DeepSeek dice: " + respuesta);
    }
}
