package mx.uatx.siia.comun.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import mx.uatx.siia.serviciosUniversitarios.constantes.Constantes;
import mx.uatx.siia.serviciosUniversitarios.dto.MensajeTO;
import mx.uatx.siia.serviciosUniversitarios.dto.ResultadoTO;
import mx.uatx.siia.serviciosUniversitarios.enums.SeveridadMensajeEnum;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class VistasHelper implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7274499531866224733L;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	FacesContext context = null;
	private Severity severidad = null;
	private String strSumario = null;
	private String strMensaje = null;

	private void obtenerContexto() {
		context = FacesContext.getCurrentInstance();
	}

	private void setearSeveridadMensaje(ResourceBundle bundle, final MensajeTO msj) {
		if (msj.getSeveridad() == SeveridadMensajeEnum.INFO.getValor()) {
            severidad = FacesMessage.SEVERITY_INFO;
            strSumario = bundle.getString("comun.label.sumario.msj.info");
        } else if (msj.getSeveridad() == SeveridadMensajeEnum.ALERTA.getValor()) {
            severidad = FacesMessage.SEVERITY_WARN;
            strSumario = bundle.getString("comun.label.sumario.msj.alerta");
        } else if (msj.getSeveridad() == SeveridadMensajeEnum.ERROR.getValor()) {
            severidad = FacesMessage.SEVERITY_ERROR;
            strSumario = bundle.getString("comun.label.sumario.msj.error");
        } else {
            severidad = FacesMessage.SEVERITY_FATAL;
            strSumario = bundle.getString("comun.label.sumario.msj.fatal");
        }
	}
	
	public void pintarMensajes(ResourceBundle bundle, final ResultadoTO resultado) {
        obtenerContexto();

        for (MensajeTO msj : resultado.getLstMensajes()) {
			
                if (msj.getMensaje() == null) {
                    continue;
                } else {
                    String strMensaje = msj.getMensaje().replace(" ", "");
                    strMensaje = strMensaje.replaceAll("\\s","");
                    if (StringUtils.isEmpty(strMensaje) || strMensaje.isEmpty() || strMensaje == "") {
                        continue;
                    }
                }
			
        	setearSeveridadMensaje(bundle, msj);
            strMensaje = bundle.getString(msj.getMensaje());
            context.addMessage("growlMsg", new FacesMessage(severidad, strSumario, strMensaje));
        }
        
        pintarMensajesValidacion(bundle, resultado.getLstMensajesValidacion());
    }

	public void pintarMensajesValidacion(ResourceBundle bundle, final List<MensajeTO> lstMensajesValidacion) {
		obtenerContexto();

		for (MensajeTO msj : lstMensajesValidacion) {
                    
                        if (msj.getMensaje() == null) {
                            continue;
			} else {
                            String strMensaje = msj.getMensaje().replace(" ", "");
                            strMensaje = strMensaje.replaceAll("\\s","");
                            if (StringUtils.isEmpty(strMensaje) || strMensaje.isEmpty() || strMensaje == "") {
				continue;
                            }
                        }
			
			setearSeveridadMensaje(bundle, msj);
			strMensaje = msj.getMensaje();
			context.addMessage("growlMsg", new FacesMessage(severidad, strSumario, strMensaje));
		}
	}

	public void descargarDocumentoNavegador(final String nombreSalida, final byte[] archivo) {
		try {
			if (nombreSalida != null && !nombreSalida.isEmpty() && archivo != null) {
				FacesContext facesContext = FacesContext.getCurrentInstance();
				HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

				response.setContentType("application/octet-stream");
				response.setHeader("Content-Disposition", "attachment;filename=" + nombreSalida.replace(" ", ""));

				ServletOutputStream out = response.getOutputStream();
				out.write(archivo);

				response.setStatus(HttpServletResponse.SC_OK);
				response.flushBuffer();
				response.getOutputStream().close();

				facesContext.responseComplete();
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}

	}

	public final String obtenerRutaServidor() {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		final String realPath = ctx.getRealPath("/");

		return realPath;
	}
	
	public String obtenerRuta(){
		String ruta = "";
		try
		{
			FacesContext context = FacesContext.getCurrentInstance();
			HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
			ruta = request.getScheme()+"://".concat(request.getServerName()).concat(":").concat(String.valueOf(request.getServerPort()));
		}catch(Exception e)
		{
			ruta = "";
			e.printStackTrace();
		}
		
		return ruta;
	}
	
	public final String obtenerRutaServidorImagenes() {
		ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		final String realPath = ctx.getRealPath("/");

		return realPath+"/resources/imagenes/";
	}

	public void llenarYObtenerBytesReporteJasperPDF(final String strRuta, final String strNombreJasper,
			final List<?> lstDatos,HashMap<String, Object> mapa) {

		JasperReport report = null;
		JasperPrint print = null;

		StringBuilder strRutaAbsoluta = new StringBuilder(obtenerRutaServidor()).append("/").append(strRuta)
				.append("/");
		StringBuilder strRutaCompletaEntrada = new StringBuilder(strRutaAbsoluta).append(strNombreJasper)
				.append(Constantes.EXT_JASPER);
		
		if(mapa == null)
			mapa = new HashMap<String, Object>();

		try {

			JRBeanCollectionDataSource colDat = new JRBeanCollectionDataSource(lstDatos);

			report = (JasperReport) JRLoader.loadObjectFromFile(strRutaCompletaEntrada.toString());
			
			print = JasperFillManager.fillReport(report, mapa, colDat);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

//			response.setContentType("application/pdf");
			response.addHeader("Content-disposition", "attachment; filename="+strNombreJasper+".pdf");
			ServletOutputStream stream = response.getOutputStream();
			JasperExportManager.exportReportToPdfStream(print, stream);
			stream.flush();
			stream.close();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
        
        public void llenarYObtenerBytesReporteJasperXLS(final String strRuta, final String strNombreJasper,
			final List<?> lstDatos,HashMap<String, Object> mapa) {

		JasperReport report = null;
		JasperPrint print = null;

		StringBuilder strRutaAbsoluta = new StringBuilder(obtenerRutaServidor()).append("/").append(strRuta)
				.append("/");
		StringBuilder strRutaCompletaEntrada = new StringBuilder(strRutaAbsoluta).append(strNombreJasper)
				.append(Constantes.EXT_JASPER);
		
		if(mapa == null)
			mapa = new HashMap<String, Object>();

		try {

			JRBeanCollectionDataSource colDat = new JRBeanCollectionDataSource(lstDatos);

			report = (JasperReport) JRLoader.loadObjectFromFile(strRutaCompletaEntrada.toString());
			
			print = JasperFillManager.fillReport(report, mapa, colDat);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();                        
                        
                        /*JRXlsExporter xlsExporter = new JRXlsExporter();

                        xlsExporter.setExporterInput(new SimpleExporterInput(print));
                        xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(strNombreJasper+".xls"));
                        SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
                        xlsReportConfiguration.setOnePagePerSheet(false);
                        xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(false);
                        xlsReportConfiguration.setDetectCellType(true);
                        xlsReportConfiguration.setWhitePageBackground(false);
                        xlsExporter.setConfiguration(xlsReportConfiguration);

                        xlsExporter.exportReport();*/
                        
                        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                        configuration.setOnePagePerSheet(true);
                        configuration.setDetectCellType(true);
                        configuration.setCollapseRowSpan(false);
                        configuration.setWhitePageBackground(false);

                        File file = new File(strNombreJasper+".xls");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        
                        response.setContentType("application/vnd.ms-excel"); 
                        response.addHeader("Content-disposition", "attachment; filename="+strNombreJasper+".xls");

                        JRXlsExporter exporterXLS = new JRXlsExporter();
                        exporterXLS.setExporterInput(new SimpleExporterInput(print));
                        exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                        exporterXLS.setConfiguration(configuration);
                        exporterXLS.exportReport();

                        fos.flush();
                        fos.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
        
        
        
        public void llenarYObtenerBytesReporteJasperXLSx(final String strRuta, final String strNombreJasper,
			final List<?> lstDatos,HashMap<String, Object> mapa) {

        	JasperReport report = null;
        	JasperPrint print = null;

		StringBuilder strRutaAbsoluta = new StringBuilder(obtenerRutaServidor()).append("/").append(strRuta)
				.append("/");
		StringBuilder strRutaCompletaEntrada = new StringBuilder(strRutaAbsoluta).append(strNombreJasper)
				.append(Constantes.EXT_JASPER);
		
		if(mapa == null)
			mapa = new HashMap<String, Object>();

		try {

			JRBeanCollectionDataSource colDat = new JRBeanCollectionDataSource(lstDatos);

			report = (JasperReport) JRLoader.loadObjectFromFile(strRutaCompletaEntrada.toString());
			
			print = JasperFillManager.fillReport(report, mapa, colDat);

			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();                        
                        
                        /*JRXlsExporter xlsExporter = new JRXlsExporter();

                        xlsExporter.setExporterInput(new SimpleExporterInput(print));
                        xlsExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(strNombreJasper+".xls"));
                        SimpleXlsReportConfiguration xlsReportConfiguration = new SimpleXlsReportConfiguration();
                        xlsReportConfiguration.setOnePagePerSheet(false);
                        xlsReportConfiguration.setRemoveEmptySpaceBetweenRows(false);
                        xlsReportConfiguration.setDetectCellType(true);
                        xlsReportConfiguration.setWhitePageBackground(false);
                        xlsExporter.setConfiguration(xlsReportConfiguration);

                        xlsExporter.exportReport();*/
			
			
					    JRXlsxExporter exporterXLS = new JRXlsxExporter();
					    
					    StringBuilder strRutaCompletaFuente = new StringBuilder(strRutaAbsoluta).append(strNombreJasper)
								.append(Constantes.EXT_JASPER_JRXML);
            
            			JasperDesign design = JRXmlLoader.load(strRutaCompletaFuente.toString());
            			
            			design.setPageHeight(3200);
                        
                        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
                        configuration.setOnePagePerSheet(true);
                        configuration.setDetectCellType(true);
                        configuration.setCollapseRowSpan(false);
                        configuration.setWhitePageBackground(false);
                        
                       

                        File file = new File(strNombreJasper+".xlsx");
                        FileOutputStream fos = new FileOutputStream(file,true);
                        
                        response.setContentType("application/vnd.ms-excel"); 
                        response.addHeader("Content-disposition", "attachment; filename="+strNombreJasper+".xlsx");


                        
                        
                        exporterXLS.setExporterInput(new SimpleExporterInput(print));
                        exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                        exporterXLS.setConfiguration(configuration);
                        exporterXLS.exportReport();

                        fos.flush();
                        fos.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void redireccionar(final String strRuta) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		try {
			context.getFlash().setKeepMessages(true);
			logger.info(context.getRequestContextPath() + strRuta);
			
			context.redirect(context.getRequestContextPath() + strRuta);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public String obtenerIpCliente() {
		String ipAddress = "";
		try {			
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			
	        if (request != null) {
	        	ipAddress = request.getHeader("X-FORWARDED-FOR");
	            if (ipAddress == null || "".equals(ipAddress)) {
	            	ipAddress = request.getRemoteAddr();
	            }
	        }
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		return ipAddress;
	}
	
	

}
