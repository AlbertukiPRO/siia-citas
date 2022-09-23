package mx.uatx.siia.citas.controller;

import mx.uatx.siia.comun.helper.VistasHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Alberto Noche Rosas
 * @date 02/08/2022
 * @apiNote Bean para el manejo del frontend del módulo citas.
 */

@ViewScoped
@ManagedBean(name = "citabean")
public class CitaController implements Serializable {

    /**
     * SerialVersion
     */
    private static final long serialVersionUID = -8116508067661315434L;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    VistasHelper vistasHelper = new VistasHelper();

    private final String rutaNuevaCita = "/vistas/citas/nuevacita.uat";

    public void nuevaCita() { vistasHelper.redireccionar(rutaNuevaCita); }

    /**
     * @LocalAtributtes
     */
}
