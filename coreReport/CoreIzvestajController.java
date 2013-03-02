package rs.fimes.web.controller.core;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectItems;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.component.html.HtmlPanelGrid;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;

import org.ajax4jsf.component.html.HtmlAjaxCommandButton;
import org.ajax4jsf.component.html.HtmlAjaxRegion;
import org.ajax4jsf.component.html.HtmlAjaxSupport;
import org.richfaces.component.html.HtmlCalendar;
import org.richfaces.component.html.HtmlRichMessage;
import org.richfaces.component.html.HtmlSpacer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import rs.etf.rc.common.application.ConfigurationException;
import rs.etf.rc.common.application.Module;
import rs.etf.rc.common.web.controller.BaseController;
import rs.fimes.domain.FimesDomain;
import rs.fimes.domain.sys.rep.ReportParameterDropDownWrapper;
import rs.fimes.domain.sys.rep.SysRepParametarReporta;
import rs.fimes.domain.sys.rep.SysRepReport;
import rs.fimes.domain.sys.rep.SysRepReportPosebni;
import rs.fimes.domain.sys.rep.SysRepReportPosebniPreset;
import rs.fimes.domain.sys.rep.SysRepReportUGrupi;
import rs.fimes.domain.sys.rep.SysRepReportUGrupiPreset;
import rs.fimes.domain.sys.rep.XsysRepFormatReporta;
import rs.fimes.domain.sys.rep.XsysRepTipParametra.XsysRepTipParametraEnum;
import rs.fimes.service.api.core.SysRepIzvestajServiceApi;
import rs.fimes.service.api.core.UsrKorisnikServiceApi;
import rs.fimes.service.api.core.XsysParametarServiceApi;
import rs.fimes.web.exception.FimesWebException;
import rs.fimes.web.jsf.converter.common.BooleanConverter;
import rs.fimes.web.jsf.converter.common.IntPozitiveConverter;
import rs.fimes.web.jsf.converter.common.IzvestajLovRequiredConverter;
import rs.fimes.web.jsf.converter.common.LongPozitiveConverter;
import rs.fimes.web.jsf.converter.common.ShortPozitiveConverter;
import rs.fimes.web.jsf.converter.common.StringRequiredConverter;

import com.sun.faces.taglib.jsf_core.SetPropertyActionListenerImpl;

public class CoreIzvestajController extends BaseController {

    private static final long serialVersionUID = 8790036104770769128L;

    public CoreIzvestajController(Module module, String controllerId)
            throws ConfigurationException {
        super(module, controllerId);
    }

    private CoreWebController coreWebController;
    
    private UsrKorisnikServiceApi usrKorisnikServiceApi;
    private SysRepIzvestajServiceApi sysRepIzvestajServiceApi;
    private XsysParametarServiceApi xsysParametarServiceApi;
    
    private List<SysRepReport> sysRepReports;
    private SysRepReport sysRepReport;
    private HtmlPanelGrid parametriGrid = new HtmlPanelGrid();
    
    /**
     * naslov za panel sa parametrima
     */
    private String parametriGridHeader;
    
    /**
     * viewId za include lov-a na stranici
     */
    private String lovViewId;
    
    //parametar za prenos preko dugmeta za otvaranje lov modal panela
    //potrebno za metode action i transfer za lov selection controller
    private String lovKey;// ime lov-a
    private String lovFieldId;// id input komponente lov-a
    
    private int i = 1;
    /**
     * Promenljiva i metoda za dohvatanje imena popup prozora
        za izvestaj koji se otvara u novom prozoru da bi uvek imao drugo ime,
        tako ce se uvek za svaki izvestaj otvoriti novi prozor.
        Ako je ime prozora isto uvek ce se svi otvarati u istom.
        
     * @return "popupWindow"+redni broj
     */
    public String getPopupWindowName() {
        if(i>100) {
            i = 1;
        }
        return "popupWindow" + i++;
    }

    
    /**
     * Poziva se na <on-start> flow-a za izvestaje nekog modula. Resetuju se prethodne promene 
     * i dohvataju svi izvestaji u grupi za dati modul.
     * 
     * @param key - id modula koji predstavlja kljuc za izvestaje koji postoje za dati modul.
     */
    public void initGrupaIzvestaja(String key) {
        setLovViewId(coreWebController.getCoreEmptyView());
        sysRepReport = null;
        setParametriGridHeader("");
        parametriGrid.getChildren().clear();
        
        List<SysRepReportUGrupi> reportiUGrupi = sysRepIzvestajServiceApi.getSysRepReportUGrupiBySysRepGrupa(key);
        sysRepReports = new ArrayList<SysRepReport>();
        for(SysRepReportUGrupi reportUGrupi: reportiUGrupi) {
            SysRepReport report = reportUGrupi.getSysRepReport();
            report.setSysRepReportUGrupi(reportUGrupi);
            sysRepReports.add(report);
        }
    }
    
    public void populatePanelGrid() {
        parametriGrid.getChildren().clear();
        
        setParametriGridHeader(getMessage("commonIzvestajParametri", getMessageArgs(sysRepReport.getSysRepReportUGrupi().getNaziv())));
        
        for(SysRepParametarReporta sysRepParametarReporta: sysRepIzvestajServiceApi.getSysRepParametarReportaBySysRepReport(sysRepReport)) {
            sysRepReport.addParameter(sysRepParametarReporta.getKey(), null);
            parametriGrid.getChildren().add(createLabel(sysRepParametarReporta.getLabela(),sysRepParametarReporta.isFRequired()));
            parametriGrid.getChildren().add(createInputComponent(sysRepParametarReporta));
            createRichMessage(sysRepParametarReporta);
        }
        
        List<XsysRepFormatReporta> formati = sysRepIzvestajServiceApi.getXsysRepFormatReportaList(sysRepReport);
        parametriGrid.getChildren().addAll(createFormatSelectFields(formati));
    }
    
    public HtmlOutputText createLabel(String name, boolean required) {
        HtmlOutputText htmlOutputText = new HtmlOutputText();
        htmlOutputText.setValue(name);
        htmlOutputText.setStyleClass(required ? "fimesLabelRequiredLeftPadding" : "fimesLabelLeftPadding");
        return htmlOutputText;
    }

    public UIComponent createInputComponent(SysRepParametarReporta repParametarReporta) {
        XsysRepTipParametraEnum tip = repParametarReporta.getXsysRepTipParametra().getXsysRepTipParametraEnum();
        switch (tip) {
            case BOOLEAN:
                return createCheckbox(repParametarReporta);
            case DATE:
                return createRichCalendar(repParametarReporta);
            case SELECT:
                return createSelectOneMenu(repParametarReporta);
            case LOV:
                return createLov(repParametarReporta);
            case LOV_INPUT:
                return createLovInput(repParametarReporta);
            default:
                HtmlInputText htmlInputText = new HtmlInputText();
                htmlInputText.setId(repParametarReporta.getKey());
                String valueExpresion = "coreIzvestajController.sysRepReport.parameterValues['" + repParametarReporta.getKey() + "']";
                htmlInputText.setValueExpression("value", createValueExpresion(valueExpresion));
                htmlInputText.setLabel(repParametarReporta.getLabela());
                if(tip!=XsysRepTipParametraEnum.STRING) {
                    htmlInputText.setConverter(createConverter(tip));
                }
                if(repParametarReporta.isFRequired()) {
                    htmlInputText.setRequired(true);
                }
                Set<SysRepReportUGrupiPreset> sysRepReportUGrupiPresets = repParametarReporta.getSysRepReportUGrupiPresets();
                if (sysRepReportUGrupiPresets != null && sysRepReportUGrupiPresets.iterator().hasNext()) {
                    SysRepReportUGrupiPreset sysRepReportUGrupiPreset = sysRepReportUGrupiPresets.iterator().next();
                    if (sysRepReportUGrupiPreset.getVrednostString() != null) {
                        htmlInputText.setValue(sysRepReportUGrupiPreset.getVrednostString());
                    }
                    else if (sysRepReportUGrupiPreset.getVrednostBroj() != null) {
                        htmlInputText.setValue(sysRepReportUGrupiPreset.getVrednostBroj());
                    }
                    else if (sysRepReportUGrupiPreset.getVrednostFunkcija() != null) {
                        htmlInputText.setValue(executeValueExpresion(sysRepReportUGrupiPreset.getVrednostFunkcija()));
                    }
                    else if (sysRepReportUGrupiPreset.getVrednostSistemskiParametar() != null) {
                        htmlInputText.setValue(getVrednostIzSistemskogParametra(sysRepReportUGrupiPreset.getVrednostSistemskiParametar()));
                    }
                }
                return htmlInputText;
        }
    }
    
    /**
     * Dohvata vrednost iz sistemskog parametra.
     * 
     * @param sysRepReportUGrupiPreset
     * @return String ili BigDecimal
     */
    private Object getVrednostIzSistemskogParametra(String vrednostSistemskiParametar) {
        Object vrednost = null;
        try {
            // prvo proverava da li parametar postoji za firmu (string, pa broj)
            vrednost = xsysParametarServiceApi.getStringParameter(
                    usrKorisnikServiceApi.getUlogovaniKorisnikIdFirma(),
                    vrednostSistemskiParametar);
            if (vrednost == null) {
                vrednost = xsysParametarServiceApi.getNumberParameter(
                        usrKorisnikServiceApi.getUlogovaniKorisnikIdFirma(),
                        vrednostSistemskiParametar);
            }
            
        } catch (Exception e) {
            // zatim proverava da li parametar postoji za ceo sistem (string, pa broj)
            try {
                vrednost = xsysParametarServiceApi.getStringParameterSystem(
                        vrednostSistemskiParametar);
                if (vrednost == null) {
                    vrednost = xsysParametarServiceApi.getNumberParameterSystem(
                            vrednostSistemskiParametar);
                }
            } catch (Exception ex) {
                // ako parametar ne postoji vraća null
                vrednost = null;
            }
        }
        return vrednost;
    }
    
    public UIComponent createCheckbox(SysRepParametarReporta repParametarReporta) {
        sysRepReport.addParameter(repParametarReporta.getKey(), new Boolean(false));
        HtmlSelectBooleanCheckbox booleanCheckbox = new HtmlSelectBooleanCheckbox();
        String valueExpresion = "coreIzvestajController.sysRepReport.parameterValues['" + repParametarReporta.getKey() + "']";
        booleanCheckbox.setValueExpression("value", createValueExpresion(valueExpresion));
        
        if (repParametarReporta.getSysRepReportUGrupiPresets() != null && repParametarReporta.getSysRepReportUGrupiPresets().size() != 0) {
            for (SysRepReportUGrupiPreset sysRepReportUGrupiPreset : repParametarReporta.getSysRepReportUGrupiPresets()) {
                if (sysRepReportUGrupiPreset.getVrednostString().toLowerCase().equals("true"))
                    booleanCheckbox.setSelected(true);
                else
                    booleanCheckbox.setSelected(false);
            }
        } else {
            booleanCheckbox.setSelected(false);
        }
        
        booleanCheckbox.setLocalValueSet(false);
        booleanCheckbox.setSubmittedValue(null);
        return booleanCheckbox;
    }

    public UIComponent createRichCalendar(SysRepParametarReporta repParametarReporta) {
        HtmlCalendar htmlCalendar = new HtmlCalendar();
        htmlCalendar.setId(repParametarReporta.getKey());
        htmlCalendar.setDatePattern(getApplication().getDateFormatMask());
        htmlCalendar.setEnableManualInput(true);
        htmlCalendar.setDirection("auto");
        htmlCalendar.setFirstWeekDay(1);
        htmlCalendar.setLabel(repParametarReporta.getLabela());
        String valueExpresion = "coreIzvestajController.sysRepReport.parameterValues['" + repParametarReporta.getKey() + "']";
        htmlCalendar.setValueExpression("value", createValueExpresion(valueExpresion));
        if(repParametarReporta.isFRequired()) {
            htmlCalendar.setRequired(true);
        }
        return htmlCalendar;
    }
    
    public UIComponent createSelectOneMenu(SysRepParametarReporta repParametarReporta) {
        HtmlSelectOneMenu htmlSelectOneMenu = new HtmlSelectOneMenu();
        String valueExpresion = "coreIzvestajController.sysRepReport.parameterValues['" + repParametarReporta.getKey() + "']";
        htmlSelectOneMenu.setValueExpression("value", createValueExpresion(valueExpresion));
        htmlSelectOneMenu.setStyleClass("fimesInputDropDown fimesReportSelectOne");
        
        UISelectItems uiSelectItems = new UISelectItems();
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        if(!repParametarReporta.isFRequired()) {
            selectItems.add(new SelectItem(0, ""));
        }
        
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
            WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
            Object serviceApi = applicationContext.getBean(repParametarReporta.getSysRepParametarDropdown().getKlasa());
            Method method = serviceApi.getClass().getDeclaredMethod(repParametarReporta.getSysRepParametarDropdown().getMetoda());
            ArrayList<?> lista = (ArrayList<?>) method.invoke(serviceApi);
            Iterator<?> iter = lista.iterator();
            while (iter.hasNext()) {
                Object object = (Object) iter.next();
                Method methodGetId = object.getClass().getDeclaredMethod(repParametarReporta.getSysRepParametarDropdown().getVrednost());
                Object id = methodGetId.invoke(object);
                
                String[] labelList = repParametarReporta.getSysRepParametarDropdown().getLabela().split(",");
                StringBuffer sbLabela = new StringBuffer();
                for(String labelPart: labelList) {
                    String[] methodParts = labelPart.split("\\.");
                    Object finalDomainObject = object;
                    for(String middleMethod : methodParts) {
                        Method methodGetLabel = finalDomainObject.getClass().getDeclaredMethod(middleMethod);
                        finalDomainObject = methodGetLabel.invoke(finalDomainObject);
                    }
                    sbLabela.append(String.valueOf(finalDomainObject));
                    sbLabela.append(" - ");
                }
                sbLabela.replace(sbLabela.length()-3, sbLabela.length(), "");
                selectItems.add(new SelectItem(id, sbLabela.toString()));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        uiSelectItems.setValue(selectItems);
        htmlSelectOneMenu.getChildren().add(uiSelectItems);
        
        return htmlSelectOneMenu;
    }
    
    public Collection<? extends UIComponent> createFormatSelectFields(List<XsysRepFormatReporta> formati) {
        List<UIComponent> formatFields = new ArrayList<UIComponent>();
        
        HtmlOutputText htmlOutputText = new HtmlOutputText();
        htmlOutputText.setValue(getMessage("commonIzvestajFormat"));
        htmlOutputText.setStyleClass("fimesLabelLeftPadding");
        formatFields.add(htmlOutputText);
        
        if(formati.size()>0) {
            HtmlSelectOneMenu htmlSelectOneMenu = new HtmlSelectOneMenu();
            String valueExpresion = "coreIzvestajController.sysRepReport.format";
            htmlSelectOneMenu.setValueExpression("value", createValueExpresion(valueExpresion));
            htmlSelectOneMenu.setStyleClass("fimesInputDropDown fimesReportSelectOne");
            
            UISelectItems uiSelectItems = new UISelectItems();
            List<SelectItem> selectItems = new ArrayList<SelectItem>();
            Iterator<XsysRepFormatReporta> iter = formati.iterator();
            while (iter.hasNext()) {
                XsysRepFormatReporta xsysRepFormatReporta = (XsysRepFormatReporta) iter
                        .next();
                selectItems.add(new SelectItem(xsysRepFormatReporta.getKey(), xsysRepFormatReporta.getNaziv()));
            }
            uiSelectItems.setValue(selectItems);
            htmlSelectOneMenu.getChildren().add(uiSelectItems);
            formatFields.add(htmlSelectOneMenu);
        }else {
            HtmlInputText inputText = new HtmlInputText();
            String format = formati.get(0).getKey();
            inputText.setValue(format);
            sysRepReport.setFormat(format);
            inputText.setReadonly(true);
            inputText.setStyleClass("fimesInputText");
            formatFields.add(inputText);
        }
        return formatFields;
    }
    
    public UIComponent createLov(SysRepParametarReporta repParametarReporta) {        
        HtmlAjaxRegion htmlAjaxRegion = new HtmlAjaxRegion();
        
        HtmlPanelGrid htmlPanelGrid = new HtmlPanelGrid();
        htmlPanelGrid.setColumns(3);
        
        HtmlInputText htmlInputText = new HtmlInputText();
        htmlInputText.setId(repParametarReporta.getKey());
        htmlInputText.setStyleClass("fimesInputText fimesReportLovInputField");
        
        //konstruisanje izraza za value expresion
        String valueExpresion = "";
        String[] lista = repParametarReporta.getSysRepParametarPopup().getZaPrikaz().split(",");
        for(String domainProperty: lista) {
            valueExpresion += "#{coreIzvestajController.sysRepReport.parameterValues['" 
                + repParametarReporta.getKey() 
                + "']."
                + domainProperty
                + "} ";
        }
        htmlInputText.setValueExpression("value", createPlainStringValueExpresion(valueExpresion));
        htmlInputText.setValueExpression("title", createPlainStringValueExpresion(valueExpresion));
        htmlInputText.setReadonly(true);
        
        String ime = repParametarReporta.getSysRepParametarPopup().getJavaDomainClass();
        String id = ime+"LovModalPanel";
        //String nameVeliko = getStringUtil().prvoSlovoVeliko(ime);
        
        HtmlAjaxCommandButton htmlAjaxCommandButton = new HtmlAjaxCommandButton();
        htmlAjaxCommandButton.setActionExpression(createExpresion("coreIzvestajController.setLovAction"));
        htmlAjaxCommandButton.setOncomplete("Richfaces.showModalPanel('"+id+"');");
        htmlAjaxCommandButton.setReRender("lovViewId");//+id
        htmlAjaxCommandButton.setStyleClass("fimesButtonImageLov");
        htmlAjaxCommandButton.setStatus("waitStatus");
        htmlAjaxCommandButton.setImmediate(true);
        htmlAjaxCommandButton.setAjaxSingle(true);
        //htmlAjaxCommandButton.setTitle(getMessage("coreLabela"+nameVeliko+"LovModalPanelIkonicaIzaberi"));
        htmlAjaxCommandButton.setTitle(getMessage("coreLabelaLovModalPanelIkonicaIzaberi"));
        ValueExpression target = createValueExpresion("coreIzvestajController.lovKey");
        ValueExpression value = createPlainStringValueExpresion(ime);
        SetPropertyActionListenerImpl propertyActionListener = new SetPropertyActionListenerImpl(target, value);
        htmlAjaxCommandButton.addActionListener(propertyActionListener);
        ValueExpression target2 = createValueExpresion("coreIzvestajController.lovFieldId");
        ValueExpression value2 = createPlainStringValueExpresion(repParametarReporta.getKey());
        SetPropertyActionListenerImpl propertyActionListener2 = new SetPropertyActionListenerImpl(target2, value2);
        htmlAjaxCommandButton.addActionListener(propertyActionListener2);
        htmlPanelGrid.getChildren().add(htmlInputText);
        htmlPanelGrid.getChildren().add(htmlAjaxCommandButton);
        
        if (repParametarReporta.isFRequired()) {
            HtmlInputHidden inputHidden = new HtmlInputHidden();
            inputHidden.setId(repParametarReporta.getKey()+"Hidden");
            inputHidden.setValue("1");
            inputHidden.setConverter(new IzvestajLovRequiredConverter());
            inputHidden.getAttributes().put("key", repParametarReporta.getKey());
            inputHidden.getAttributes().put("labela", repParametarReporta.getLabela());
            htmlPanelGrid.getChildren().add(inputHidden);
        }else {
            HtmlAjaxCommandButton resetCommandButton = new HtmlAjaxCommandButton();
            resetCommandButton
                    .setActionExpression(createExpresion("coreIzvestajController.resetLovObject"));
            resetCommandButton.setImmediate(true);
            resetCommandButton.setAjaxSingle(true);
            resetCommandButton.setReRender(repParametarReporta.getKey());
            resetCommandButton.setStyleClass("fimesButtonImageCross");
            resetCommandButton.setTitle(getMessage("coreLabelaLovModalPanelIkonicaObrisi"));
            resetCommandButton.addActionListener(propertyActionListener2);
            htmlPanelGrid.getChildren().add(resetCommandButton);
        }
                
        htmlAjaxRegion.getChildren().add(htmlPanelGrid);
        
        String controllerName = null;
        try {
            controllerName = repParametarReporta.getSysRepParametarPopup().getController();
            if(controllerName==null) {
                controllerName = repParametarReporta.getSysRepParametarPopup().getJavaDomainClass()+"SelectionController";
            }
            initControllerMethod(controllerName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("kontroler "+controllerName+" nema metodu initForReport()");
            e.printStackTrace();
        }
        
        return htmlAjaxRegion;
    }
    
    public void setLovAction() throws Exception {        
        
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        //ako selection controller nije imenovan po konvenciji kao sto je to slucaj za gkKonto
        //kontroler se zove gkKontoLovSelectionController a ne gkKontoSelectionController.
        //tada je u SysRepParametarPopup().getController() zapisano ime kontrolera
        //inace je po konvenciji "osbOsobaSelectionController" gde je osbOsoba ime domain klase
        String controllerName = sysRepIzvestajServiceApi.getSysRepParametarReportaByKey(sysRepReport, lovFieldId).getSysRepParametarPopup().getController();
        if(controllerName==null) {
            controllerName = lovKey+"SelectionController";
        }
        
        Object selectionController = applicationContext.getBean(controllerName);
        
        String modulId = null;
        
            Method methodAction = selectionController.getClass().getDeclaredMethod("setDugmeAction",String.class);
            methodAction.invoke(selectionController, "coreIzvestajController.transferLovObject");
            
            Method methodRerender = selectionController.getClass().getDeclaredMethod("setDugmeReRender",String.class);
            methodRerender.invoke(selectionController, lovFieldId+",lovViewId");
            
            try {
                initControllerMethod(controllerName);
            } catch (Exception e) {
                System.out.println("kontroler "+controllerName+" nema metodu initForReport()");
                e.printStackTrace();
            }
            
            try {
                Method methodPretraga = selectionController.getClass().getDeclaredMethod("pretraga");
                methodPretraga.invoke(selectionController);
            } catch (Exception e) {
                sendExceptionMail(e);
                String message = "kontroler '"+controllerName+"' nema metodu pretraga() !!! - proveriti podatke pri prvom otvaranju modal panel-a";
                FimesWebException fwe = new FimesWebException(message);
                sendExceptionMail(fwe);
            }
            
            Method methodModulId = selectionController.getClass().getMethod("getModule");
            Module module = (Module) methodModulId.invoke(selectionController);
            modulId = module.getFullId(); // TODO ovde je bilo shortId - treba svuda promeniti?!
            if(modulId.contains(".")) {
                String tempId = "";
                String[] temp = modulId.split("\\.");
                for (int i = 0; i < temp.length; i++) {
                    if(i==0) {
                        tempId += temp[i];
                    }else {
                        tempId += getStringUtil().prvoSlovoVeliko(temp[i]);
                    }
                }
                modulId = tempId;
            }
            //System.out.println("modul id: "+modulId);
         /*catch (NoSuchMethodException e) {
            
            //todo: ovo je u redu za lovInput jer se vrednost ne koristi ali za obican lov bi trebalo novo polje...
            controllerName = sysRepIzvestajServiceApi.getSysRepParametarReportaByKey(sysRepReport, lovFieldId).getSysRepParametarPopup().getZaPrikaz();
            selectionController = applicationContext.getBean(controllerName);
            Method methodAction = selectionController.getClass().getDeclaredMethod("setDugmeAction",String.class);
            methodAction.invoke(selectionController, "coreIzvestajController.transferLovObject");
            
            Method methodRerender = selectionController.getClass().getDeclaredMethod("setDugmeReRender",String.class);
            methodRerender.invoke(selectionController, lovFieldId+",lovViewId");
            
            Method methodModulId = selectionController.getClass().getMethod("getModule");
            Module module = (Module) methodModulId.invoke(selectionController);
            modulId = module.getShortId();
        }*/
        
        Object coreWebController = applicationContext.getBean(modulId+"WebController");
        String getLovViewMethodName = "get"+getStringUtil().prvoSlovoVeliko(lovKey)+"LovModalPanelView";
        
        Method methodSetLovView = coreWebController.getClass().getDeclaredMethod(getLovViewMethodName);
        String result = (String) methodSetLovView.invoke(coreWebController);
        
        setLovViewId(result);
    }
    
    public void transferLovObject() throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        //ako selection controller nije imenovan po konvenciji kao sto je to slucaj za gkKonto
        //kontroler se zove gkKontoLovSelectionController a ne gkKontoSelectionController.
        //tada je u SysRepParametarPopup().getController() zapisano ime kontrolera
        //inace je po konvenciji "osbOsobaSelectionController" gde je osbOsoba ime domain klase
        String controllerName = sysRepIzvestajServiceApi.getSysRepParametarReportaByKey(sysRepReport, lovFieldId).getSysRepParametarPopup().getController();
        if(controllerName==null) {
            controllerName = lovKey+"SelectionController";
        }
        
        Object selectionController = applicationContext.getBean(controllerName);
        
        Object rezult = null;
      
            Method methodAction = selectionController.getClass().getDeclaredMethod("get"+getStringUtil().prvoSlovoVeliko(lovKey));
            rezult = methodAction.invoke(selectionController);
        /*} catch (NoSuchMethodException e) {
            //ako selection controller nije imenovan po konvenciji kao sto je to slucaj za gkKonto
            //kontroler se zove gkKontoLovSelectionController a ne gkKontoSelectionController.
            //tada je u SysRepParametarPopup().getVrednost() zapisano ime kontrolera
            //todo: ovo je u redu za lovInput jer se vrednost ne koristi ali za obican lov bi trebalo novo polje...
            try {
                controllerName = sysRepIzvestajServiceApi.getSysRepParametarReportaByKey(sysRepReport, lovFieldId).getSysRepParametarPopup().getVrednost();
                selectionController = applicationContext.getBean(controllerName);
                Method methodAction = selectionController.getClass().getDeclaredMethod("get"+getStringUtil().prvoSlovoVeliko(lovKey));
                System.out.println("kontroler je "+controllerName);
                System.out.println("metoda pozvana: "+"get"+getStringUtil().prvoSlovoVeliko(lovKey));
                rezult = methodAction.invoke(selectionController);
                System.out.println("rezult je "+rezult);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }*/
        sysRepReport.addParameter(lovFieldId, rezult);
        setLovViewId(coreWebController.getCoreEmptyView());
    }
    
    public void resetLovObject() {
        sysRepReport.addParameter(lovFieldId, null);
    }
    
    /**
     * @param repParametarReporta
     * @return
     */
    public UIComponent createLovInput(SysRepParametarReporta repParametarReporta) {        
        HtmlAjaxRegion htmlAjaxRegion = new HtmlAjaxRegion();
        
        HtmlPanelGrid htmlPanelGrid = new HtmlPanelGrid();
        htmlPanelGrid.setColumns(2);
                        
        //readonly input za prikaz vrednosti odabrane iz lov-a
        HtmlInputText htmlInputText = new HtmlInputText();
        htmlInputText.setId(repParametarReporta.getKey());
        htmlInputText.setLabel(repParametarReporta.getLabela());
        htmlInputText.setStyleClass("fimesInputText");
        
        //moram da inicijalizujem vrednost parametra, za to mi treba dinamicki da dohvatim
        //domain klasu i da pozovem konstruktor
        String path = "";
        String basePath = getApplication().getJavaBasePath();
        String domainName = repParametarReporta.getSysRepParametarPopup().getJavaDomainClass();
        String domain = getStringUtil().prvoSlovoVeliko(domainName);
            
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ServletContext servletContext = (ServletContext) facesContext.getExternalContext().getContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        String controllerName = repParametarReporta.getSysRepParametarPopup().getController();
        if(controllerName==null) {
            controllerName = domainName+"SelectionController";
        }
        Object selectionController = applicationContext.getBean(controllerName);
        Module module = null;
        try {
            Method methodModulId = selectionController.getClass().getMethod("getModule");
            module = (Module) methodModulId.invoke(selectionController);
        } catch (Exception e) {
            e.printStackTrace();
            sendExceptionMail(e);
        }
        String modulId = module.getShortId();
        path = basePath + "." + modulId + ".";
        try {            
            String classPath = basePath + ".domain." + modulId + "." + domain;
            path = basePath + ".domain." + modulId + ".";
            sysRepReport.addParameter(repParametarReporta.getKey(), Class.forName(classPath).newInstance());
        } catch (Exception e) {
            try {
                //neke domain klase su u data.domain paketu (smor)
                String classPath = basePath + ".data.domain." + modulId + "." + domain;
                path = basePath + ".data.domain." + modulId + ".";
                sysRepReport.addParameter(repParametarReporta.getKey(), Class.forName(classPath).newInstance());
            } catch (Exception e2) {
                e2.printStackTrace();
                sendExceptionMail(e2);
            }
        }
        
        //todo ako repParametarReporta.getSysRepParametarPopup().getVrednost()
        //sadrzi tacku onda treba da se inicijalizuje i taj property
        //treba da se proveri ako ima vise nivoa...
        String[] lista = repParametarReporta.getSysRepParametarPopup().getVrednost().split("\\.");
        int num = lista.length-1;
        Object domainKlasa = null;
        for(int i=0; i<num; i++) {
            if(i==0) {
                domainKlasa = sysRepReport.getParameterValues().get(repParametarReporta.getKey());
            }else {
                try {
                    Method method = domainKlasa.getClass().getMethod("get"+getStringUtil().prvoSlovoVeliko(lista[i-1]));
                    domainKlasa = method.invoke(domainKlasa);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            String property = lista[i];
            try {
                Method method = domainKlasa.getClass().getMethod("set"+getStringUtil().prvoSlovoVeliko(property),Class.forName(path+getStringUtil().prvoSlovoVeliko(property)));
                method.invoke(domainKlasa, Class.forName(path+getStringUtil().prvoSlovoVeliko(property)).newInstance());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
      //izraz za value expresion
        String valueExpresion = "#{coreIzvestajController.sysRepReport.parameterValues['" 
                + repParametarReporta.getKey() 
                + "']."
                + repParametarReporta.getSysRepParametarPopup().getVrednost()
                + "}";
        
        htmlInputText.setValueExpression("value", createPlainStringValueExpresion(valueExpresion));
        htmlInputText.setValueExpression("title", createPlainStringValueExpresion(valueExpresion));
        htmlInputText.setRequired(repParametarReporta.isFRequired());
        
        String ime = repParametarReporta.getSysRepParametarPopup().getJavaDomainClass();
        String id = ime+"LovModalPanel";
        
        HtmlAjaxCommandButton htmlAjaxCommandButton = new HtmlAjaxCommandButton();
        htmlAjaxCommandButton.setActionExpression(createExpresion("coreIzvestajController.setLovAction"));
        htmlAjaxCommandButton.setOncomplete("Richfaces.showModalPanel('"+id+"');");
        htmlAjaxCommandButton.setReRender("lovViewId");
        htmlAjaxCommandButton.setStyleClass("fimesButtonImageLov");
        htmlAjaxCommandButton.setStatus("waitStatus");
        htmlAjaxCommandButton.setImmediate(true);
        htmlAjaxCommandButton.setAjaxSingle(true);
        htmlAjaxCommandButton.setTitle(getMessage("coreLabelaLovModalPanelIkonicaIzaberi"));
        ValueExpression target = createValueExpresion("coreIzvestajController.lovKey");
        ValueExpression value = createPlainStringValueExpresion(ime);
        SetPropertyActionListenerImpl propertyActionListener = new SetPropertyActionListenerImpl(target, value);
        htmlAjaxCommandButton.addActionListener(propertyActionListener);
        ValueExpression target2 = createValueExpresion("coreIzvestajController.lovFieldId");
        ValueExpression value2 = createPlainStringValueExpresion(repParametarReporta.getKey());
        SetPropertyActionListenerImpl propertyActionListener2 = new SetPropertyActionListenerImpl(target2, value2);
        htmlAjaxCommandButton.addActionListener(propertyActionListener2);
        
        htmlPanelGrid.getChildren().add(htmlInputText);
        htmlPanelGrid.getChildren().add(htmlAjaxCommandButton);
                
        htmlAjaxRegion.getChildren().add(htmlPanelGrid);
        
        try {
            initControllerMethod(controllerName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("kontroler "+controllerName+" nema metodu initForReport()");
            e.printStackTrace();
        }
        
        return htmlAjaxRegion;
    }
    
    public UIComponent createLovInputOnChange(SysRepParametarReporta repParametarReporta) {        
        HtmlAjaxRegion htmlAjaxRegion = new HtmlAjaxRegion();
        
        HtmlPanelGrid htmlPanelGrid = new HtmlPanelGrid();
        htmlPanelGrid.setColumns(3);
        
        //input za unos
        HtmlInputText htmlLovInput = new HtmlInputText();
        htmlLovInput.setId(repParametarReporta.getKey()+"LovInput");
        htmlLovInput.setStyleClass("fimesInputText");
        htmlLovInput.setValueExpression("value", createPlainStringValueExpresion("" +
                "#{coreIzvestajController.sysRepReport.parameterValues['" 
                + repParametarReporta.getKey() 
                + "']."
                + repParametarReporta.getSysRepParametarPopupInput().getVrednost()
                + "} "));
        htmlLovInput.setValueExpression("label", createPlainStringValueExpresion(repParametarReporta.getLabela()));
        htmlLovInput.setValueExpression("validator", createPlainStringValueExpresion("#{coreIzvestajController.validateLovInput}"));
        htmlLovInput.setRequired(true);
        HtmlAjaxSupport ajaxSupport = new HtmlAjaxSupport();
        ajaxSupport.setEvent("onchange");
        ajaxSupport.setAjaxSingle(true);
        ajaxSupport.setReRender(repParametarReporta.getKey()+"LovInput");
        htmlLovInput.getChildren().add(ajaxSupport);
        
        //readonly input za prikaz vrednosti odabrane iz lov-a
        HtmlInputText htmlInputText = new HtmlInputText();
        htmlInputText.setId(repParametarReporta.getKey());
        htmlInputText.setStyleClass("fimesInputText fimesReportLovInputField");
        
            //konstruisanje izraza za value expresion
        String valueExpresion = "";
        String[] lista = repParametarReporta.getSysRepParametarPopup().getZaPrikaz().split(",");
        for(String domainProperty: lista) {
            valueExpresion += "#{coreIzvestajController.sysRepReport.parameterValues['" 
                + repParametarReporta.getKey() 
                + "']."
                + domainProperty
                + "} ";
        }
        htmlInputText.setValueExpression("value", createPlainStringValueExpresion(valueExpresion));
        htmlInputText.setValueExpression("title", createPlainStringValueExpresion(valueExpresion));
        htmlInputText.setReadonly(true);
        
        String ime = repParametarReporta.getSysRepParametarPopup().getJavaDomainClass();
        String id = ime+"LovModalPanel";
        //String nameVeliko = getStringUtil().prvoSlovoVeliko(ime);
        
        HtmlAjaxCommandButton htmlAjaxCommandButton = new HtmlAjaxCommandButton();
        htmlAjaxCommandButton.setActionExpression(createExpresion("coreIzvestajController.setLovAction"));
        htmlAjaxCommandButton.setOncomplete("Richfaces.showModalPanel('"+id+"');");
        htmlAjaxCommandButton.setReRender("lovViewId");//+id
        htmlAjaxCommandButton.setStyleClass("fimesButtonImageLov");
        htmlAjaxCommandButton.setImmediate(true);
        htmlAjaxCommandButton.setAjaxSingle(true);
        //htmlAjaxCommandButton.setTitle(getMessage("coreLabela"+nameVeliko+"LovModalPanelIkonicaIzaberi"));
        htmlAjaxCommandButton.setTitle(getMessage("coreLabelaLovModalPanelIkonicaIzaberi"));
        ValueExpression target = createValueExpresion("coreIzvestajController.lovKey");
        ValueExpression value = createPlainStringValueExpresion(ime);
        SetPropertyActionListenerImpl propertyActionListener = new SetPropertyActionListenerImpl(target, value);
        htmlAjaxCommandButton.addActionListener(propertyActionListener);
        ValueExpression target2 = createValueExpresion("coreIzvestajController.lovFieldId");
        ValueExpression value2 = createPlainStringValueExpresion(repParametarReporta.getKey());
        SetPropertyActionListenerImpl propertyActionListener2 = new SetPropertyActionListenerImpl(target2, value2);
        htmlAjaxCommandButton.addActionListener(propertyActionListener2);
        
        htmlPanelGrid.getChildren().add(htmlLovInput);
        htmlPanelGrid.getChildren().add(htmlInputText);
        htmlPanelGrid.getChildren().add(htmlAjaxCommandButton);
                
        htmlAjaxRegion.getChildren().add(htmlPanelGrid);
        
        return htmlAjaxRegion;
    }
    
    public void validateLovInput(FacesContext context,
            UIComponent validate, Object value) {
        
        String broj = (String) value;
        
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        WebApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
        
        String serviceBeanName = "prjKontoServiceApi";
        Object serviceBean = applicationContext.getBean(serviceBeanName);
        
        Object rezult = null;
        try {
            Method methodAction = serviceBean.getClass().getDeclaredMethod("getPrjKontoByBrojKonta");
            rezult = methodAction.invoke(serviceBean,broj);
          //PrjKonto konto = prjKontoServiceApi.getPrjKontoByBrojKonta(broj);
        }catch (Exception e) {
            // TODO: handle exception
        }
        if(rezult==null) {
            //prjKontoDugovna = new PrjKonto();
            //setPrjKontoDugovnaBrojKontaFocus("prjKontoDugovnaBrojKonta");
            FacesMessage msg = new FacesMessage(getMessage(
                    "prjKontoValidateBrojakonta",
                    getMessageArgs(broj)));
            context.addMessage(validate.getClientId(context), msg);
            context.renderResponse();
        }else {
            //prjKontoDugovna = konto;
            //setPrjKontoDugovnaBrojKontaFocus("prjKontoPotraznaBrojKonta");
        }
        sysRepReport.addParameter(lovFieldId, rezult);
    }

    public void createRichMessage(SysRepParametarReporta sysRepParametarReporta) {
        String messageKey = sysRepParametarReporta.getKey();
        if(sysRepParametarReporta.getXsysRepTipParametra().getXsysRepTipParametraEnum().compareTo(XsysRepTipParametraEnum.LOV)==0) {
            messageKey += "Hidden";
        }
        parametriGrid.getChildren().add(new HtmlSpacer());
        HtmlRichMessage htmlRichMessage = new HtmlRichMessage();
        htmlRichMessage.setFor(messageKey);
        htmlRichMessage.setStyleClass("fimesValidErrMsg");
        parametriGrid.getChildren().add(htmlRichMessage);
    }
    
    private void initControllerMethod(String controllerName) {
        if (controllerName == null || controllerName.trim().isEmpty()) return;
        FacesContext ctx = FacesContext.getCurrentInstance();
        javax.faces.application.Application app = ctx.getApplication();
        String actionMethod = "#{" + controllerName + ".initForReport}";
        MethodExpression actionExpression = app.getExpressionFactory()
                .createMethodExpression(ctx.getELContext(), actionMethod,
                        null, new Class<?>[0]);
        actionExpression.invoke(ctx.getELContext(),null);
    }
    
    /*public UIComponent createModalPanel(ModalPanelParameters modalPanelParameters) {
        String name = modalPanelParameters.getName();
        String id = name+"LovModalPanel";
        String nameVeliko = getStringUtil().prvoSlovoVeliko(name);
        String header = "coreLabela"+nameVeliko+"LovModalPanelHeader";
        String tableName = "tabela"+nameVeliko;
        String controller = name+"SelectionController";
        String datamodel = name+"DataTableModelApi";
        String tableValue = controller+"."+datamodel;
        String tableAction = controller+".handleSelection";
        String dugmeId = name+"DugmeOdaberi";
        
        HtmlModalPanel htmlModalPanel = new HtmlModalPanel();
        htmlModalPanel.setId(id);
        htmlModalPanel.setAutosized(true);
        htmlModalPanel.setResizeable(false);
        htmlModalPanel.setMoveable(true);
        htmlModalPanel.setMinWidth(600);
        
        Map<String, UIComponent> facets = htmlModalPanel.getFacets();
        HtmlOutputText htmlOutputText = new HtmlOutputText();
        htmlOutputText.setValue(getMessage(header));
        facets.put("header", htmlOutputText);
        
        HtmlHotKey htmlHotKey = new HtmlHotKey();
        htmlHotKey.setKey("backspace");
        htmlHotKey.setHandler("return false;");
        htmlHotKey.setDisableInInput(true);
        htmlModalPanel.getChildren().add(htmlHotKey);
        
        HtmlExtendedDataTable htmlExtendedDataTable = new HtmlExtendedDataTable();
        htmlExtendedDataTable.setId(tableName);
        htmlExtendedDataTable.setStyle("margin: auto;");
        htmlExtendedDataTable.setHeight("310px");
        htmlExtendedDataTable.setWidth("800px");
        htmlExtendedDataTable.setRows(10);
        htmlExtendedDataTable.setEnableContextMenu(false);
        //htmlExtendedDataTable.setValueExpression("tableState", createValueExpresion("coreIzvestajController.tableState"));
        htmlExtendedDataTable.setNoDataLabel(getMessage("commonNoDataLabel"));
        htmlExtendedDataTable.setValueExpression("value", createValueExpresion(tableValue,OsbOsobaDataTableModelApi.class));
        htmlExtendedDataTable.setVar(name);
        
        htmlExtendedDataTable.setSortMode("single");
        htmlExtendedDataTable.setSelectionMode("single");
        htmlExtendedDataTable.setValueExpression("selection", createValueExpresion("coreIzvestajController.selection", SimpleSelection.class));
        htmlExtendedDataTable.setRowKeyVar("rkvar");
        htmlExtendedDataTable.setValueExpression("tableState", createValueExpresion("coreIzvestajController.tableState"));
        
        HtmlAjaxSupport htmlAjaxSupport = new HtmlAjaxSupport();
        htmlAjaxSupport.setEvent("onRowClick");
        htmlAjaxSupport.setActionExpression(createExpresion(tableAction));
        htmlAjaxSupport.setReRender(dugmeId);
        htmlExtendedDataTable.getChildren().add(htmlAjaxSupport);
        
        HtmlColumn htmlColumn = new HtmlColumn();
        htmlColumn.setId("column1ime");
        htmlColumn.setWidth("100%");
        htmlColumn.setVisible(true);
        htmlColumn.setSortable(false);
        
        HtmlOutputText columnLabel = new HtmlOutputText();
        columnLabel.setValue("ime");
        htmlColumn.setHeader(columnLabel);
        
        HtmlOutputText htmlOutputText2 = new HtmlOutputText();
        String value = name+".ime";
        htmlOutputText2.setValueExpression("value", createValueExpresion(value));
        
        htmlColumn.getChildren().add(htmlOutputText2);
        
        htmlExtendedDataTable.getChildren().add(htmlColumn);
        
        HtmlColumn htmlColumn2 = new HtmlColumn();
        htmlColumn2.setId("column1ime");
        htmlColumn2.setWidth("100%");
        htmlColumn2.setVisible(true);
        htmlColumn2.setSortable(false);
        
        HtmlOutputText columnLabel2 = new HtmlOutputText();
        columnLabel2.setValue("prezime");
        htmlColumn2.setHeader(columnLabel2);
        
        HtmlOutputText htmlOutputText3 = new HtmlOutputText();
        String value2 = name+".prezime";
        htmlOutputText3.setValueExpression("value", createValueExpresion(value2));
        
        htmlColumn2.getChildren().add(htmlOutputText3);
        
        htmlExtendedDataTable.getChildren().add(htmlColumn2);
        
        int i = 0;
        for(TableColumnProperty column: modalPanelParameters.getColumns()) {
            HtmlColumn htmlColumn = new HtmlColumn();
            String columnName = column.getName();
            htmlColumn.setId(columnName);
            htmlColumn.setWidth(column.getColumnWidth());
                        
            HtmlPanelGrid htmlPanelGrid = new HtmlPanelGrid();
            htmlPanelGrid.setColumns(1);
            htmlPanelGrid.setStyleClass("fimesTableCentered");
            
            HtmlAjaxCommandLink htmlAjaxCommandLink = new HtmlAjaxCommandLink();
            String columnLabel = "coreLabela"+nameVeliko+"LovModalPanel"+getStringUtil().prvoSlovoVeliko(columnName);
            htmlAjaxCommandLink.setValue(getMessage(columnLabel));
            htmlAjaxCommandLink.setActionExpression(createExpresion(controller+".odrediSort"));
            String tableRerender = tableName+" "+dugmeId;
            htmlAjaxCommandLink.setReRender(tableRerender);
            
            HtmlActionParameter htmlActionParameter = new HtmlActionParameter();
            htmlActionParameter.setName("sortField");
            htmlActionParameter.setValue(columnName);
            String sortField = controller+"."+datamodel+".sortField";
            htmlActionParameter.setAssignToBinding(createValueExpresion(sortField));
            htmlAjaxCommandLink.getChildren().add(htmlActionParameter);
            htmlPanelGrid.getChildren().add(htmlAjaxCommandLink);
            
            HtmlInputText htmlInputText = new HtmlInputText();
            htmlInputText.setTabindex(String.valueOf(++i));
            String inputValue = controller+"."+columnName;
            htmlInputText.setValueExpression("value", createValueExpresion(inputValue));
            
            HtmlAjaxSupport htmlAjaxSupport2 = new HtmlAjaxSupport();
            htmlAjaxSupport2.setEvent("onchange");
            String action = controller+".pretraga";
            htmlAjaxSupport2.setActionExpression(createExpresion(action));
            htmlAjaxSupport2.setAjaxSingle(true);
            htmlAjaxSupport2.setIgnoreDupResponses(true);
            htmlAjaxSupport2.setReRender(tableRerender);
            htmlInputText.getChildren().add(htmlAjaxSupport2);
            htmlPanelGrid.getChildren().add(htmlInputText);
            
            Map<String, UIComponent> columnFacets = htmlColumn.getFacets();
            columnFacets.put("header", htmlPanelGrid);
            
            HtmlOutputText htmlOutputText2 = new HtmlOutputText();
            String value = name+"."+columnName;
            htmlOutputText2.setValueExpression("value", createValueExpresion(value));
            
            htmlColumn.getChildren().add(htmlOutputText2);
            
            htmlExtendedDataTable.getChildren().add(htmlColumn);
        }
        HtmlForm form = new HtmlForm();
        form.getChildren().add(htmlExtendedDataTable);
        htmlModalPanel.getChildren().add(form);
        
        //dugme potvrdi odustani
        HtmlPanelGroup htmlPanelGroup = new HtmlPanelGroup();
        htmlPanelGroup.setLayout("block");
        htmlPanelGroup.setStyleClass("alignCenter fimesTopPadding");
        
        HtmlAjaxCommandButton potvrdiCommandButton = new HtmlAjaxCommandButton();
        potvrdiCommandButton.setId(name+"DugmeOdaberi");
        potvrdiCommandButton.setStyleClass("fimesButton fimesButtonSubmit fimesButtonImageOk");
        potvrdiCommandButton.setValue(getMessage("common_odaberi"));
        //potvrdiCommandButton.setActionExpression(createExpresion("coreIzvestajController.transferOsbOsoba"));
        String binding = controller+".dugme";
        potvrdiCommandButton.setValueExpression("binding", createValueExpresion(binding));
        potvrdiCommandButton.setOncomplete("Richfaces.hideModalPanel('"+id+"');");
        potvrdiCommandButton.setReRender(name);
        String disabled = "empty "+controller+"."+name;
        //potvrdiCommandButton.setValueExpression("disabled", createValueExpresion(disabled));
        htmlPanelGroup.getChildren().add(potvrdiCommandButton);
        
        //proba za command link ako moze umesto dugmeta potvrdi
        
        HtmlAjaxCommandLink htmlAjaxCommandLink = new HtmlAjaxCommandLink();
        htmlAjaxCommandLink.setId(name+"DugmeOdaberi");
        htmlAjaxCommandLink.setStyleClass("fimesButton fimesButtonSubmit fimesButtonImageOk");
        htmlAjaxCommandLink.setValue(getMessage("common_odaberi"));
        htmlAjaxCommandLink.setActionExpression(createExpresion(controller+".odrediSort"));
        String tableRerender = tableName+" "+dugmeId;
        htmlAjaxCommandLink.setReRender(tableRerender);
        
        HtmlActionParameter htmlActionParameter = new HtmlActionParameter();
        htmlActionParameter.setName("sortField");
        htmlActionParameter.setValue(columnName);
        String sortField = controller+"."+datamodel+".sortField";
        htmlActionParameter.setAssignToBinding(createValueExpresion(sortField));
        htmlAjaxCommandLink.getChildren().add(htmlActionParameter);
        htmlPanelGrid.getChildren().add(htmlAjaxCommandLink);
        
        //gotovo za probu
        
        HtmlSpacer spacer = new HtmlSpacer();
        spacer.setWidth("5");
        htmlPanelGroup.getChildren().add(spacer);
        
        HtmlAjaxCommandButton odustaniCommandButton = new HtmlAjaxCommandButton();
        odustaniCommandButton.setStyleClass("fimesButton fimesButtonImageCancel");
        odustaniCommandButton.setValue(getMessage("common_odustani"));
        odustaniCommandButton.setOnclick("Richfaces.hideModalPanel('"+id+"');");
        htmlPanelGroup.getChildren().add(odustaniCommandButton);
        
        htmlModalPanel.getChildren().add(htmlPanelGroup);
        
        return htmlModalPanel;
    }*/
        
    //todo dodati konverter i kada nije required za one kojima treba (moguce svi)
    private Converter createConverter(XsysRepTipParametraEnum tip) {
        Converter converter = null;
        switch (tip) {
            case STRING:
                //todo sta cemo sa max length za stringove ?
                return new StringRequiredConverter();
            case INTEGER:
                return new IntPozitiveConverter();
            case BOOLEAN:
                return new BooleanConverter();
            case SHORT:
                return new ShortPozitiveConverter();
            case DOUBLE:
                return new LongPozitiveConverter();
            default:
                break;
        }
        return converter;
    }
    
    public void reportSave() {
      //todo ostaviti samo runReport
        //System.out.println(sysRepReport);
        getJasperReportsUtil().runReport("ipAdressUnknown", usrKorisnikServiceApi.ulogovaniKorisnik().getIdKorisnik(), "attachment", sysRepReport.getFormat(), sysRepReport.getKey(), createParametersString());
    }
    
    public void reportView() {
      //todo ostaviti samo runReport
        //System.out.println(sysRepReport);
        getJasperReportsUtil().runReport("ipAdressUnknown", usrKorisnikServiceApi.ulogovaniKorisnik().getIdKorisnik(), "inline", sysRepReport.getFormat(), sysRepReport.getKey(), createParametersString());
    }
    
    /**
     * @return String sa parametrima za pokretanje izvestaja.
     */
    public String createParametersString() {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : sysRepReport
                .getParameterValues().entrySet()) {
            String key = entry.getKey();
            sb.append("&" + key);
            Object value = entry.getValue();
            sb.append("=");
            if (value != null) {
                if (value instanceof Boolean) {
                    Boolean bul = (Boolean) value;
                    sb.append(bul ? 1 : 0);
                } else if (value instanceof Date) {
                    Date date = (Date) value;
                    sb.append(getDateTimeUtil().formatDatumDefault(date));
                } else if (value instanceof String) {
                    //ako je tip parametra select one menu, tada se za prosledjenu vrednost 0 vraca prazan string jer 0 oznacava da nista nije izabrano
                    SysRepParametarReporta parametar = sysRepIzvestajServiceApi
                    .getSysRepParametarReportaByKey(sysRepReport, key);
                    if(parametar.getXsysRepTipParametra().getXsysRepTipParametraEnum().compareTo(XsysRepTipParametraEnum.SELECT)==0) {
                        String sel = (String) value;
                        sb.append(sel.equalsIgnoreCase("0") ? "" : sel);
                    }else {
                        sb.append(value);
                    }
                } else if (value instanceof FimesDomain) {
                    //za vrednosti iz lov-a pamti se ceo objekat, pa se iz baze uzima naziv get metode cija vrednost treba da se prosledi
                    SysRepParametarReporta parametar = sysRepIzvestajServiceApi
                            .getSysRepParametarReportaByKey(sysRepReport, key);
                    try {
                        /*Method method = value
                                .getClass()
                                .getDeclaredMethod(
                                        "get"
                                                + getStringUtil()
                                                        .prvoSlovoVeliko(
                                                                parametar
                                                                        .getSysRepParametarPopup()
                                                                        .getVrednost()));
                        Object rezult = method.invoke(value);*/
                        String valueExpresion = "coreIzvestajController.sysRepReport.parameterValues['" 
                            + parametar.getKey() 
                            + "']."
                            + parametar.getSysRepParametarPopup().getVrednost();
                        Object rezult = executeValueExpresion(valueExpresion);
                        sb.append(rezult);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {                    
                    sb.append(value);
                }
            }
        }
        //predefinisane vrednosti - parametri sa predefinisanim vrednostima mogu biti string, broj ili vrednost neke funkcije.
        //ove vrednosti su zapisane u bazi u tabeli sys_rep_report_u_grupi_preset
        for (SysRepReportUGrupiPreset reportUGrupiPreset : sysRepIzvestajServiceApi.getSysRepParametarReportaPresetBySysRepReport(sysRepReport)) {
            sb.append("&").append(reportUGrupiPreset.getSysRepParametarReporta().getKey());
            String vrednost = "";
            
            if (reportUGrupiPreset.getVrednostString() != null) {
                vrednost = reportUGrupiPreset.getVrednostString().trim();
                
            } else if (reportUGrupiPreset.getVrednostBroj() != null) {
                try {
                    vrednost = getNumberUtil().getBigDecimalNumberString(reportUGrupiPreset.getVrednostBroj());
                } catch (ParseException e) {
                }
                
            } else if (reportUGrupiPreset.getVrednostFunkcija() != null) {
                vrednost = (String) executeValueExpresion(reportUGrupiPreset.getVrednostFunkcija());

            } else if (reportUGrupiPreset.getVrednostSistemskiParametar() != null) {
                Object vrednostParametra = getVrednostIzSistemskogParametra(reportUGrupiPreset.getVrednostSistemskiParametar());
                if (vrednostParametra instanceof BigDecimal) {
                    try {
                        vrednost = getNumberUtil().getBigDecimalNumberString((BigDecimal)vrednostParametra);
                    } catch (ParseException e) {
                    }
                } else {
                    vrednost = (String) vrednostParametra;
                }
            }
            sb.append("=").append(vrednost);
        }
        return sb.toString();
    }
    
    //pojedinacni report
    
    //
    /**
     * SysRepReportPosebni.key - dohvatanje pojedinacnog izvestaja po kljucu
     */
    private String key;
    
    
    /**
     * Dohvatanje pojedinacnog izvestaja po kljucu 'key' koji se prosledjuje sa stranice, primer:
     * 
     *          <a4j:commandButton id="dugmeIzvestajView"
                    value="#{coreIzvestajController.module.messageSource.common_izvestaj}"
                    styleClass="fimesButton fimesButtonSubmit"
                    action="#{coreIzvestajController.generatePojedinacniIzvestaj}"
                    status="waitStatus" immediate="true"
                    ajaxSingle="true"
                    oncomplete="if(#{empty facesContext.maximumSeverity}){window.open('#{jasperReportsUtil.reportUrl}', '#{coreIzvestajController.popupWindowName}', 'dependent=yes, menubar=no, toolbar=no');}">
                    <f:setPropertyActionListener
                      value="fimes.gk.gk_nalog"
                      target="#{coreIzvestajController.key}" />
                  </a4j:commandButton>
     */
    public void generatePojedinacniIzvestaj() {
        SysRepReportPosebni reportPosebni = sysRepIzvestajServiceApi.getSysRepReportPosebniByKey(key);
        StringBuffer sb = new StringBuffer();
        //pojedinacni izvestaji za sada imaju samo predefinisane parametre.
        //vrednosti se nalaze u tabeli sys_rep_report_posebni_preset
        for (SysRepReportPosebniPreset reportPosebniPreset : sysRepIzvestajServiceApi.getsSysRepReportPosebniPresets(reportPosebni)) {
            sb.append("&").append(reportPosebniPreset.getSysRepParametarReporta().getKey());
            String vrednost = "";
            
            if (reportPosebniPreset.getVrednostString() != null) {
                vrednost = reportPosebniPreset.getVrednostString().trim();
                
            } else if (reportPosebniPreset.getVrednostBroj() != null) {
                try {
                    vrednost = getNumberUtil().getBigDecimalNumberString(reportPosebniPreset.getVrednostBroj());
                } catch (ParseException e) {
                }
                
            } else if (reportPosebniPreset.getVrednostFunkcija() != null) {
                vrednost = (String) executeValueExpresion(reportPosebniPreset.getVrednostFunkcija());
                
            } else if (reportPosebniPreset.getVrednostSistemskiParametar() != null) {
                Object vrednostParametra = getVrednostIzSistemskogParametra(reportPosebniPreset.getVrednostSistemskiParametar());
                if (vrednostParametra instanceof BigDecimal) {
                    try {
                        vrednost = getNumberUtil().getBigDecimalNumberString((BigDecimal)vrednostParametra);
                    } catch (ParseException e) {
                    }
                } else {
                    vrednost = (String) vrednostParametra;
                }
            }
            sb.append("=").append(vrednost);
        }
        getJasperReportsUtil().runReport("ipAdressUnknown", usrKorisnikServiceApi.ulogovaniKorisnik().getIdKorisnik(), "inline", "pdf", reportPosebni.getSysRepReport().getKey(), sb.toString());
    }
    
    // za izvestaje RAC_KPR_stavke_projekti i RAC_KIR_stavke_projekti
    // jer se pozivaju iz vise modula (PRJ,KPR,KIR)
    public List<ReportParameterDropDownWrapper> getPFPredracunList() {
        List<ReportParameterDropDownWrapper> dropDownList = new ArrayList<ReportParameterDropDownWrapper>();
        dropDownList.add(new ReportParameterDropDownWrapper(0, getMessage("repSve")));
        dropDownList.add(new ReportParameterDropDownWrapper(1, getMessage("repRacune")));
        dropDownList.add(new ReportParameterDropDownWrapper(2, getMessage("repPredracune")));
        return dropDownList;
    }
    public List<ReportParameterDropDownWrapper> getPFPlacenoList() {
        List<ReportParameterDropDownWrapper> dropDownList = new ArrayList<ReportParameterDropDownWrapper>();
        dropDownList.add(new ReportParameterDropDownWrapper(0, getMessage("repSve")));
        dropDownList.add(new ReportParameterDropDownWrapper(1, getMessage("repNeplacene")));
        dropDownList.add(new ReportParameterDropDownWrapper(2, getMessage("repPlacene")));
        return dropDownList;
    }

    // get set

    public void setParametriGrid(HtmlPanelGrid parametriGrid) {
        this.parametriGrid = parametriGrid;
    }

    public HtmlPanelGrid getParametriGrid() {
        return parametriGrid;
    }

    public void setLovViewId(String lovViewId) {
        this.lovViewId = lovViewId;
    }

    public String getLovViewId() {
        return lovViewId;
    }

    public CoreWebController getCoreWebController() {
        return coreWebController;
    }

    public void setCoreWebController(CoreWebController coreWebController) {
        this.coreWebController = coreWebController;
    }

    public void setUsrKorisnikServiceApi(UsrKorisnikServiceApi usrKorisnikServiceApi) {
        this.usrKorisnikServiceApi = usrKorisnikServiceApi;
    }

    public UsrKorisnikServiceApi getUsrKorisnikServiceApi() {
        return usrKorisnikServiceApi;
    }

    public void setLovKey(String lovKey) {
        this.lovKey = lovKey;
    }

    public String getLovKey() {
        return lovKey;
    }

    public void setSysRepIzvestajServiceApi(SysRepIzvestajServiceApi sysRepIzvestajServiceApi) {
        this.sysRepIzvestajServiceApi = sysRepIzvestajServiceApi;
    }

    public SysRepIzvestajServiceApi getSysRepIzvestajServiceApi() {
        return sysRepIzvestajServiceApi;
    }

    public void setSysRepReports(List<SysRepReport> sysRepReports) {
        this.sysRepReports = sysRepReports;
    }

    public List<SysRepReport> getSysRepReports() {
        return sysRepReports;
    }

    public void setSysRepReport(SysRepReport sysRepReport) {
        this.sysRepReport = sysRepReport;
    }

    public SysRepReport getSysRepReport() {
        return sysRepReport;
    }

    public void setParametriGridHeader(String parametriGridHeader) {
        this.parametriGridHeader = parametriGridHeader;
    }

    public String getParametriGridHeader() {
        return parametriGridHeader;
    }

    public void setLovFieldId(String lovFieldId) {
        this.lovFieldId = lovFieldId;
    }

    public String getLovFieldId() {
        return lovFieldId;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public XsysParametarServiceApi getXsysParametarServiceApi() {
        return xsysParametarServiceApi;
    }

    public void setXsysParametarServiceApi(XsysParametarServiceApi xsysParametarServiceApi) {
        this.xsysParametarServiceApi = xsysParametarServiceApi;
    }

}
