package mx.uatx.siia.citas;

import mx.uatx.siia.citas.pruebas.Prueba;
import mx.uatx.siia.comun.helper.VistasHelper;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class Reporte {

    public static void GenerarReporte() throws JRException {
        VistasHelper vistasHelper = new VistasHelper();

        //CitasTO emp1 = new CitasTO(20181837, 1, 1, "descripcion tramite", "agendada","15/05/2022", "08:50");

        Prueba emp1 = new Prueba("20181837","Alberto N.R");
        List<Prueba> empLst = new ArrayList<Prueba>();
        empLst.add(emp1);

        //dynamic parameters required for report
        Map<String, Object> empParams = new HashMap<>();
        empParams.put("UATx", "Comprobante de Cita");
        empParams.put("Datos Citas", new JRBeanCollectionDataSource(empLst));

        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("strMatricula","2018183");
        parametros.put("strNombre","Alberto Noche Rosas");

//        File jasperFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reporte.jasper"));
//
//        System.out.println("Path: "+jasperFile.getPath());

        try {
//            JasperPrint empReport = JasperFillManager.fillReport(jasperFile.getPath(), parametros, new JRBeanCollectionDataSource(empLst));
//            System.out.println("---RUN MAKE PDF REPORT---");
//            vistasHelper.descargarDocumentoNavegador("Comprobante cita",JasperExportManager.exportReportToPdf(empReport));

            JasperPrint jasperPrint = JasperFillManager.fillReport(new ClassPathResource("/Reporte.jasper").getInputStream(), parametros, new JREmptyDataSource());
            vistasHelper.descargarDocumentoNavegador("Comprobante cita.pdf", JasperExportManager.exportReportToPdf(jasperPrint));
//            JRPdfExporter pdfExporter = new JRPdfExporter();
//            pdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//            pdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput("reportOutput"));
//            pdfExporter.exportReport();
        } catch (JRException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}