package test.java;

import View.ModuloPrincipalET4;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import static org.hamcrest.MatcherAssert.assertThat;

// Casos por logro de la Etapa 4 (resources/semantico2/logros/<logro>/), sólo
// para los logros ya implementados (spec 0003, "Testing Decisions"). Una spec
// de logro nueva sólo agrega el nombre a LOGROS_HABILITADOS.
// A diferencia de TesterConErroresSem2, un caso con error puede esperar
// varios errores: cada línea inicial "///[...]" es una etiqueta y tienen que
// aparecer todas (logro Multi-detección).
@RunWith(Parameterized.class)
public class TesterLogrosSem2 {

    private static final String[] LOGROS_HABILITADOS = {"multiDeteccionErrores", "postfijos"};
    private static final String directorioLogros = "resources/semantico2/logros/";
    private static final String msgExito = "[SinErrores]";

    private static final ModuloPrincipalET4 init = null;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private boolean fullCompilerOuputPrintingInEachTest = true;

    @Before
    public void setUpClass() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDownClass() {
        System.setOut(originalOut);
    }

    // Cada parámetro es "<logro>/<conErrores|sinErrores>/<archivo>".
    @Parameters(name = "{0}")
    public static Iterable<? extends Object> data() {
        ArrayList<String> casos = new ArrayList<>();
        for (String logro : LOGROS_HABILITADOS) {
            for (String carpeta : new String[]{"conErrores", "sinErrores"}) {
                File folder = new File(directorioLogros + logro + "/" + carpeta);
                File[] archivos = folder.listFiles();
                if (archivos == null) {
                    continue;
                }
                for (File f : archivos) {
                    casos.add(logro + "/" + carpeta + "/" + f.getName());
                }
            }
        }
        casos.sort(String::compareTo);
        return casos;
    }

    private final String caso;

    public TesterLogrosSem2(String caso) {
        this.caso = caso;
    }

    @Test
    public void testIterado() {
        String path = directorioLogros + caso;
        String[] args = {path};
        init.main(args);

        if (fullCompilerOuputPrintingInEachTest) {
            System.setOut(originalOut);
            System.out.println(outContent.toString());
        }

        if (caso.contains("/conErrores/")) {
            List<String> etiquetas = etiquetasEsperadas(path);
            assertThat("El caso no declara ninguna etiqueta ///[...]: " + path,
                    etiquetas.isEmpty(), CoreMatchers.is(false));
            for (String etiqueta : etiquetas) {
                assertThat("No se encontro el codigo: " + etiqueta + " en " + path,
                        outContent.toString(), CoreMatchers.containsString(etiqueta));
            }
        } else {
            assertThat("Mensaje Incorrecto en: " + path,
                    outContent.toString(), CoreMatchers.containsString(msgExito));
        }
    }

    // Las líneas iniciales que empiezan con "///", sin ese prefijo (mismo
    // corte de 3 caracteres que TesterConErroresSem2).
    private static List<String> etiquetasEsperadas(String path) {
        List<String> etiquetas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(path))) {
            String linea;
            while ((linea = lector.readLine()) != null && linea.startsWith("///")) {
                etiquetas.add(linea.substring(3));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return etiquetas;
    }
}
