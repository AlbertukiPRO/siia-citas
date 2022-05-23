package mx.uatx.siia.reportes;

import mx.uatx.siia.comun.helper.VistasHelper;
import mx.uatx.siia.serviciosUniversitarios.dto.CitasTO;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ManagedBean(name="reporte")
@RequestScoped
public class ControllerReporte {

    public void ExportarPDF() throws JRException, IOException {


        Map<String, Object> parametros = new HashMap<String, Object>();
        parametros.put("strMatricula","2018183");
        parametros.put("strNombre","Alberto Noche Rosas");

        File jasperFile = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("/Reporte.jasper"));



    }

}