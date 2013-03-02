package rs.fimes.web.controller.core;

import rs.etf.rc.common.application.ConfigurationException;
import rs.etf.rc.common.application.Module;
import rs.etf.rc.common.web.WebComponent;
import rs.etf.rc.common.web.exception.CommonWebException;
import rs.fimes.web.controller.BaseWebController;

public class CoreWebController extends BaseWebController {

    public CoreWebController(Module module, String shortId)
            throws ConfigurationException {
        super(module, shortId);
    }

    private static final long serialVersionUID = -4809797451603656010L;

    private WebComponent ajaxStatusModalPanel;
    private WebComponent coreModalOkDijalog;
    private WebComponent coreModalErrorDijalog;
    private WebComponent coreModalDaNeDijalog;
    private WebComponent coreModalDaNeOdustaniDijalog;
    private WebComponent coreModalUserPassPotvrdaDijalog;

    private WebComponent usrKorisnikLovModalPanel;
    private WebComponent coreNazivRequiredModalPanel;

    // fragmenti
    private WebComponent coreModalDijalog;
    private WebComponent coreModalLov;
    private WebComponent coreModalCrud;
    private WebComponent coreFragmentOsbOsoba;
    private WebComponent coreFragmentPpPoslovniPartner;

    private WebComponent coreEmpty;

    // ikonice
    private WebComponent iconLovModalPanel;
    private WebComponent iconAdd;
    private WebComponent iconCross;
    private WebComponent iconClose16;
    private WebComponent iconFolderBlue;
    private WebComponent iconBack;
    private WebComponent iconForward;
    private WebComponent iconUp;
    private WebComponent iconDown;
    private WebComponent iconInfo;
    private WebComponent iconInfo16;
    private WebComponent iconSuma;
    private WebComponent iconLogout;
    private WebComponent iconArchive16;
    private WebComponent iconBullet;

    private WebComponent iconNext32;
    private WebComponent iconPrevious32;
    private WebComponent iconFirst32;
    private WebComponent iconLast32;
    private WebComponent iconUp32;
    private WebComponent iconDown32;

    private WebComponent iconAddMulti;

    private WebComponent imageLoad;

    private WebComponent zzOperativaStavkaNovaModalPanel;
    private WebComponent usrAktivacijaKorisnickogNalogaModalPanel;
    private WebComponent usrNoviKorisnikModalPanel;
    private WebComponent usrKorisnikResetSifreModalPanel;

    public String getAjaxStatusModalPanelView() {
        try {
            return getNavigationUtil().getView("ajaxStatusModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreModalOkDijalogView() {
        try {
            return getNavigationUtil().getView("coreModalOkDijalog");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreModalErrorDijalogView() {
        try {
            return getNavigationUtil().getView("coreModalErrorDijalog");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreModalDaNeDijalogView() {
        try {
            return getNavigationUtil().getView("coreModalDaNeDijalog");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreModalDaNeOdustaniDijalogView() {
        try {
            return getNavigationUtil().getView("coreModalDaNeOdustaniDijalog");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreModalUserPassPotvrdaDijalogView() {
        try {
            return getNavigationUtil().getView(
                    "coreModalUserPassPotvrdaDijalog");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getUsrKorisnikLovModalPanelView() {
        try {
            return getNavigationUtil().getView("usrKorisnikLovModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreEmptyView() {
        try {
            return getNavigationUtil().getView("coreEmpty");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreNazivRequiredModalPanelView() {
        try {
            return getNavigationUtil().getView("coreNazivRequiredModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getZzOperativaStavkaNovaModalPanelView() {
        try {
            return getNavigationUtil().getView(
                    "zzOperativaStavkaNovaModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getUsrAktivacijaKorisnickogNalogaModalPanelView() {
        try {
            return getNavigationUtil().getView(
                    "usrAktivacijaKorisnickogNalogaModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getUsrNoviKorisnikModalPanelView() {
        try {
            return getNavigationUtil().getView("usrNoviKorisnikModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getUsrKorisnikResetSifreModalPanelView() {
        try {
            return getNavigationUtil().getView(
                    "usrKorisnikResetSifreModalPanel");
        } catch (CommonWebException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "null";
        }
    }

    public String getCoreModalDijalogView() {
        return getCoreModalDijalog().getViewPath()
                + getNavigationUtil().getWebComponentDefaultSuffix();
    }

    public String getCoreModalLovView() {
        return getCoreModalLov().getViewPath()
                + getNavigationUtil().getWebComponentDefaultSuffix();
    }

    public String getCoreModalCrudView() {
        return getCoreModalCrud().getViewPath()
                + getNavigationUtil().getWebComponentDefaultSuffix();
    }

    public String getCoreFragmentOsbOsobaView() {
        return getCoreFragmentOsbOsoba().getViewPath()
                + getNavigationUtil().getWebComponentDefaultSuffix();
    }

    public String getCoreFragmentPpPoslovniPartnerView() {
        return getCoreFragmentPpPoslovniPartner().getViewPath()
                + getNavigationUtil().getWebComponentDefaultSuffix();
    }

    public String getIconLovModalPanelPath() {
        return getNavigationUtil().getIconPath(iconLovModalPanel.getShortId());
    }

    public String getIconAddPath() {
        return getNavigationUtil().getIconPath(iconAdd.getShortId());
    }

    public String getIconAddMultiPath() {
        return getNavigationUtil().getIconPath(iconAddMulti.getShortId());
    }

    public String getIconBackPath() {
        return getNavigationUtil().getIconPath(iconBack.getShortId());
    }

    public String getIconCrossPath() {
        return getNavigationUtil().getIconPath(iconCross.getShortId());
    }

    public String getIconDownPath() {
        return getNavigationUtil().getIconPath(iconDown.getShortId());
    }

    public String getIconFolderBluePath() {
        return getNavigationUtil().getIconPath(iconFolderBlue.getShortId());
    }

    public String getIconForwardPath() {
        return getNavigationUtil().getIconPath(iconForward.getShortId());
    }

    public String getIconInfoPath() {
        return getNavigationUtil().getIconPath(iconInfo.getShortId());
    }
    
    public String getIconInfo16Path() {
        return getNavigationUtil().getIconPath(iconInfo16.getShortId());
    }

    public String getIconClose16Path() {
        return getNavigationUtil().getIconPath(iconClose16.getShortId());
    }

    public String getIconSumaPath() {
        return getNavigationUtil().getIconPath(iconSuma.getShortId());
    }

    public String getIconLogoutPath() {
        return getNavigationUtil().getIconPath(iconLogout.getShortId());
    }

    public String getIconUpPath() {
        return getNavigationUtil().getIconPath(iconUp.getShortId());
    }

    public String getImageLoadPath() {
        return getNavigationUtil().getImagePath(imageLoad.getShortId());
    }

    public String getIconNext32Path() {
        return getNavigationUtil().getIconPath(iconNext32.getShortId());
    }

    public String getIconPrevious32Path() {
        return getNavigationUtil().getIconPath(iconPrevious32.getShortId());
    }

    public String getIconFirst32Path() {
        return getNavigationUtil().getIconPath(iconFirst32.getShortId());
    }

    public String getIconLast32Path() {
        return getNavigationUtil().getIconPath(iconLast32.getShortId());
    }

    public String getIconUp32Path() {
        return getNavigationUtil().getIconPath(iconUp32.getShortId());
    }

    public String getIconDown32Path() {
        return getNavigationUtil().getIconPath(iconDown32.getShortId());
    }

    public String getIconArchive16Path() {
        return getNavigationUtil().getIconPath(iconArchive16.getShortId());
    }

    public String getIconBulletPath() {
        return getNavigationUtil().getIconPath(iconBullet.getShortId());
    }

    // get set
    public void setCoreModalOkDijalog(WebComponent coreModalOkDijalog) {
        this.coreModalOkDijalog = coreModalOkDijalog;
    }

    public WebComponent getCoreModalOkDijalog() {
        return coreModalOkDijalog;
    }

    public void setCoreModalDaNeDijalog(WebComponent coreModalDaNeDijalog) {
        this.coreModalDaNeDijalog = coreModalDaNeDijalog;
    }

    public WebComponent getCoreModalDaNeDijalog() {
        return coreModalDaNeDijalog;
    }

    public void setCoreModalDaNeOdustaniDijalog(
            WebComponent coreModalDaNeOdustaniDijalog) {
        this.coreModalDaNeOdustaniDijalog = coreModalDaNeOdustaniDijalog;
    }

    public WebComponent getCoreModalDaNeOdustaniDijalog() {
        return coreModalDaNeOdustaniDijalog;
    }

    public void setAjaxStatusModalPanel(WebComponent ajaxStatusModalPanel) {
        this.ajaxStatusModalPanel = ajaxStatusModalPanel;
    }

    public WebComponent getAjaxStatusModalPanel() {
        return ajaxStatusModalPanel;
    }

    public WebComponent getCoreModalUserPassPotvrdaDijalog() {
        return coreModalUserPassPotvrdaDijalog;
    }

    public void setCoreModalUserPassPotvrdaDijalog(
            WebComponent coreModalUserPassPotvrdaDijalog) {
        this.coreModalUserPassPotvrdaDijalog = coreModalUserPassPotvrdaDijalog;
    }

    public WebComponent getUsrKorisnikLovModalPanel() {
        return usrKorisnikLovModalPanel;
    }

    public void setUsrKorisnikLovModalPanel(
            WebComponent usrKorisnikLovModalPanel) {
        this.usrKorisnikLovModalPanel = usrKorisnikLovModalPanel;
    }

    public void setCoreEmpty(WebComponent coreEmpty) {
        this.coreEmpty = coreEmpty;
    }

    public WebComponent getCoreEmpty() {
        return coreEmpty;
    }

    public void setIconLovModalPanel(WebComponent iconLovModalPanel) {
        this.iconLovModalPanel = iconLovModalPanel;
    }

    public WebComponent getIconLovModalPanel() {
        return iconLovModalPanel;
    }

    public WebComponent getIconAdd() {
        return iconAdd;
    }

    public void setIconAdd(WebComponent iconAdd) {
        this.iconAdd = iconAdd;
    }

    public WebComponent getIconCross() {
        return iconCross;
    }

    public void setIconCross(WebComponent iconCross) {
        this.iconCross = iconCross;
    }

    public WebComponent getIconFolderBlue() {
        return iconFolderBlue;
    }

    public void setIconFolderBlue(WebComponent iconFolderBlue) {
        this.iconFolderBlue = iconFolderBlue;
    }

    public WebComponent getIconBack() {
        return iconBack;
    }

    public void setIconBack(WebComponent iconBack) {
        this.iconBack = iconBack;
    }

    public WebComponent getIconForward() {
        return iconForward;
    }

    public void setIconForward(WebComponent iconForward) {
        this.iconForward = iconForward;
    }

    public WebComponent getIconUp() {
        return iconUp;
    }

    public void setIconUp(WebComponent iconUp) {
        this.iconUp = iconUp;
    }

    public WebComponent getIconDown() {
        return iconDown;
    }

    public void setIconDown(WebComponent iconDown) {
        this.iconDown = iconDown;
    }

    public WebComponent getIconInfo() {
        return iconInfo;
    }

    public void setIconInfo(WebComponent iconInfo) {
        this.iconInfo = iconInfo;
    }

    public WebComponent getIconInfo16() {
        return iconInfo16;
    }

    public void setIconInfo16(WebComponent iconInfo16) {
        this.iconInfo16 = iconInfo16;
    }

    public WebComponent getIconSuma() {
        return iconSuma;
    }

    public void setIconSuma(WebComponent iconSuma) {
        this.iconSuma = iconSuma;
    }

    public WebComponent getIconLogout() {
        return iconLogout;
    }

    public void setIconLogout(WebComponent iconLogout) {
        this.iconLogout = iconLogout;
    }

    public void setImageLoad(WebComponent imageLoad) {
        this.imageLoad = imageLoad;
    }

    public WebComponent getImageLoad() {
        return imageLoad;
    }

    public WebComponent getIconNext32() {
        return iconNext32;
    }

    public void setIconNext32(WebComponent iconNext32) {
        this.iconNext32 = iconNext32;
    }

    public WebComponent getIconPrevious32() {
        return iconPrevious32;
    }

    public void setIconPrevious32(WebComponent iconPrevious32) {
        this.iconPrevious32 = iconPrevious32;
    }

    public WebComponent getIconFirst32() {
        return iconFirst32;
    }

    public void setIconFirst32(WebComponent iconFirst32) {
        this.iconFirst32 = iconFirst32;
    }

    public WebComponent getIconLast32() {
        return iconLast32;
    }

    public void setIconLast32(WebComponent iconLast32) {
        this.iconLast32 = iconLast32;
    }

    public WebComponent getIconUp32() {
        return iconUp32;
    }

    public void setIconUp32(WebComponent iconUp32) {
        this.iconUp32 = iconUp32;
    }

    public WebComponent getIconDown32() {
        return iconDown32;
    }

    public void setIconDown32(WebComponent iconDown32) {
        this.iconDown32 = iconDown32;
    }

    public void setCoreModalErrorDijalog(WebComponent coreModalErrorDijalog) {
        this.coreModalErrorDijalog = coreModalErrorDijalog;
    }

    public WebComponent getCoreModalErrorDijalog() {
        return coreModalErrorDijalog;
    }

    public void setIconClose16(WebComponent iconClose16) {
        this.iconClose16 = iconClose16;
    }

    public WebComponent getIconClose16() {
        return iconClose16;
    }

    public void setIconAddMulti(WebComponent iconAddMulti) {
        this.iconAddMulti = iconAddMulti;
    }

    public WebComponent getIconAddMulti() {
        return iconAddMulti;
    }

    public void setCoreNazivRequiredModalPanel(
            WebComponent coreNazivRequiredModalPanel) {
        this.coreNazivRequiredModalPanel = coreNazivRequiredModalPanel;
    }

    public WebComponent getCoreNazivRequiredModalPanel() {
        return coreNazivRequiredModalPanel;
    }

    public void setZzOperativaStavkaNovaModalPanel(
            WebComponent zzOperativaStavkaNovaModalPanel) {
        this.zzOperativaStavkaNovaModalPanel = zzOperativaStavkaNovaModalPanel;
    }

    public WebComponent getZzOperativaStavkaNovaModalPanel() {
        return zzOperativaStavkaNovaModalPanel;
    }

    public void setIconArchive16(WebComponent iconArchive16) {
        this.iconArchive16 = iconArchive16;
    }

    public WebComponent getIconArchive16() {
        return iconArchive16;
    }

    public WebComponent getCoreModalDijalog() {
        return coreModalDijalog;
    }

    public void setCoreModalDijalog(WebComponent coreModalDijalog) {
        this.coreModalDijalog = coreModalDijalog;
    }

    public WebComponent getCoreModalLov() {
        return coreModalLov;
    }

    public void setCoreModalLov(WebComponent coreModalLov) {
        this.coreModalLov = coreModalLov;
    }

    public WebComponent getCoreModalCrud() {
        return coreModalCrud;
    }

    public void setCoreModalCrud(WebComponent coreModalCrud) {
        this.coreModalCrud = coreModalCrud;
    }

    public WebComponent getCoreFragmentOsbOsoba() {
        return coreFragmentOsbOsoba;
    }

    public void setCoreFragmentOsbOsoba(WebComponent coreFragmentOsbOsoba) {
        this.coreFragmentOsbOsoba = coreFragmentOsbOsoba;
    }

    public WebComponent getCoreFragmentPpPoslovniPartner() {
        return coreFragmentPpPoslovniPartner;
    }

    public void setCoreFragmentPpPoslovniPartner(
            WebComponent coreFragmentPpPoslovniPartner) {
        this.coreFragmentPpPoslovniPartner = coreFragmentPpPoslovniPartner;
    }

    public void setIconBullet(WebComponent iconBullet) {
        this.iconBullet = iconBullet;
    }

    public WebComponent getIconBullet() {
        return iconBullet;
    }

    public WebComponent getUsrAktivacijaKorisnickogNalogaModalPanel() {
        return usrAktivacijaKorisnickogNalogaModalPanel;
    }

    public void setUsrAktivacijaKorisnickogNalogaModalPanel(
            WebComponent usrAktivacijaKorisnickogNalogaModalPanel) {
        this.usrAktivacijaKorisnickogNalogaModalPanel = usrAktivacijaKorisnickogNalogaModalPanel;
    }

    public WebComponent getUsrNoviKorisnikModalPanel() {
        return usrNoviKorisnikModalPanel;
    }

    public void setUsrNoviKorisnikModalPanel(
            WebComponent usrNoviKorisnikModalPanel) {
        this.usrNoviKorisnikModalPanel = usrNoviKorisnikModalPanel;
    }

    public WebComponent getUsrKorisnikResetSifreModalPanel() {
        return usrKorisnikResetSifreModalPanel;
    }

    public void setUsrKorisnikResetSifreModalPanel(
            WebComponent usrKorisnikResetSifreModalPanel) {
        this.usrKorisnikResetSifreModalPanel = usrKorisnikResetSifreModalPanel;
    }

}
