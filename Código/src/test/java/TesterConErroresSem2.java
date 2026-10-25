package test.java;

import View.ModuloPrincipalET4;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TesterConErroresSem2 {
    
    // Sem2 corre sobre el main de la etapa 4 (declaraciones + sentencias).
    private static final ModuloPrincipalET4 init = null;
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private static final String testFilesDirectoryPath = "resources/semantico2/conErrores/";
    private static final Pattern etiquetaDeError = Pattern.compile("\\[Error:[^\\]]*\\]");
    private boolean fullCompilerOuputPrintingInEachTest = true;
     
    @Before
    public  void setUpClass() {
        System.setOut(new PrintStream(outContent));
    }
    
    @After
    public  void tearDownClass() {
        System.setOut(originalOut);
    }
    
    @Parameters(name = "{0}")
    public static Iterable<? extends Object> data() {
        File folder = new File(testFilesDirectoryPath);
        ArrayList<String> names = new ArrayList();
        for(File f: folder.listFiles()){
            names.add(f.getName());
        }
        names.sort(String::compareTo);
        return names;
    }
    
    private String input;
    
    public TesterConErroresSem2(String input){
        this.input = input;
    }
       
        
    @Test
    public void test1() {
        probarFallo(input);
    }

    private void probarFallo(String name) {
        String testCaseFilePath = testFilesDirectoryPath+name;
        String errorCode = getErrorCode(testCaseFilePath);
        String[] args = {testCaseFilePath};
        init.main(args);

        if(fullCompilerOuputPrintingInEachTest){
            System.setOut(originalOut);
            System.out.println(outContent.toString());
        }

        // La etiqueta esperada tiene que ser la PRIMERA de la salida, no sólo
        // aparecer: con Multi-detección, un error previo fuera de orden o en
        // cascada (spec 0003) pasaría con containsString.
        assertThat("La primera etiqueta de la salida no es la esperada",
                primeraEtiqueta(outContent.toString()), CoreMatchers.is(errorCode));
    }

    private static String primeraEtiqueta(String texto) {
        Matcher m = etiquetaDeError.matcher(texto);
        return m.find() ? m.group() : null;
    }

    // Se toma la etiqueta con la expresión regular y no con un corte fijo de
    // caracteres, porque los casos de la cátedra la escriben con "//" y no "///".
    String getErrorCode(String testCaseFilePath)  {
        String lineWithTheCode = null;
        try {
            lineWithTheCode = (new BufferedReader(new FileReader(testCaseFilePath))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return primeraEtiqueta(lineWithTheCode);
    }





    

    
    
    
    
}
