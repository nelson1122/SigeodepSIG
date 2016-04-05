/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.util;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RowDataTable class is responsible for giving the appropriate structure to
 * tables so they can be used appropriately for PrimeFaces.
 *
 * @author SANTOS
 */
public class RowDataTable {

    private String column1;
    private String column2;
    private String column3;
    private String column4;
    private String column5;
    private String column6;
    private String column7;
    private String column8;
    private String column9;
    private String column10;
    private String column11;
    private String column12;
    private String column13;
    private String column14;
    private String column15;
    private String column16;
    private String column17;
    private String column18;
    private String column19;
    private String column20;
    private String column21;
    private String column22;
    private String column23;
    private String column24;
    private String column25;
    private String column26;
    private String column27;
    private String column28;
    private String column29;
    private String column30;
    private String column31;
    private String column32;
    private String column33;
    private String column34;
    private String column35;
    private String column36;
    private String column37;
    private String column38;
    private String column39;
    private String column40;
    private String column41;
    private String column42;
    private String column43;
    private String column44;
    private String column45;
    private String column46;
    private String column47;
    private String column48;
    private String column49;
    private String column50;
    private String column51;
    private String column52;
    private String column53;
    private String column54;
    private String column55;
    private String column56;
    private String column57;
    private String column58;
    private String column59;
    private String column60;
    private String column61;
    private String column62;
    private String column63;
    private String column64;
    private String column65;
    private String column66;
    private String column67;
    private String column68;
    private String column69;
    private String column70;
    private String column71;
    private String column72;
    private String column73;
    private String column74;
    private String column75;
    private String column76;
    private String column77;
    private String column78;
    private String column79;
    private String column80;
    private String column81;
    private String column82;
    private String column83;
    private String column84;
    private String column85;
    private String column86;
    private String column87;
    private String column88;
    private String column89;
    private String column90;
    private String column91;
    private String column92;
    private String column93;
    private String column94;
    private String column95;
    private String column96;
    private String column97;
    private String column98;
    private String column99;
    private String column100;
    private String column101;
    private String column102;
    private String column103;
    private String column104;
    private String column105;
    private String column106;
    private String column107;
    private String column108;
    private String column109;
    private String column110;
    private String column111;
    private String column112;
    private String column113;
    private String column114;
    private String column115;
    private String column116;
    private String column117;
    private String column118;
    private String column119;
    private String column120;
    private String column121;
    private String column122;
    private String column123;
    private String column124;
    private String column125;
    private String column126;
    private String column127;
    private String column128;
    private String column129;
    private String column130;

    public RowDataTable() {
        this.column1 = "";
        this.column2 = "";
        this.column3 = "";
        this.column4 = "";
        this.column5 = "";
        this.column6 = "";
        this.column7 = "";
        this.column8 = "";
        this.column9 = "";
        this.column10 = "";
    }

    public void resetear() throws NoSuchFieldException {
        Field[] propieties;
        propieties = this.getClass().getFields();
        for (int i = 0; i < propieties.length; i++) {
            try {
                Field field = propieties[i];
                this.getClass().getField(field.getName()).set(this.getClass().newInstance(), "sss");
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(RowDataTable.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public RowDataTable(String column1, String column2, String column3, String column4, String column5, String column6, String column7, String column8, String column9, String column10) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
        this.column7 = column7;
        this.column8 = column8;
        this.column9 = column9;
        this.column10 = column10;
    }

    public RowDataTable(String column1, String column2, String column3, String column4, String column5, String column6, String column7, String column8, String column9) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
        this.column7 = column7;
        this.column8 = column8;
        this.column9 = column9;

    }

    public RowDataTable(String column1, String column2, String column3, String column4, String column5, String column6, String column7, String column8) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
        this.column7 = column7;
        this.column8 = column8;
    }

    public RowDataTable(String column1, String column2, String column3, String column4, String column5, String column6, String column7) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
        this.column7 = column7;
    }

    public RowDataTable(String column1, String column2, String column3, String column4, String column5, String column6) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
        this.column6 = column6;
    }

    public RowDataTable(String column1, String column2, String column3, String column4, String column5) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
    }

    public RowDataTable(String column1, String column2, String column3, String column4) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
    }

    public RowDataTable(String column1, String column2, String column3) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
    }

    public RowDataTable(String column1, String column2) {
        this.column1 = column1;
        this.column2 = column2;
    }

    public RowDataTable(String column1) {
        this.column1 = column1;
    }

    public String getColumn1() {
        return column1;
    }

    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    public String getColumn10() {
        return column10;
    }

    public void setColumn10(String column10) {
        this.column10 = column10;
    }

    public String getColumn2() {
        return column2;
    }

    public void setColumn2(String column2) {
        this.column2 = column2;
    }

    public String getColumn3() {
        return column3;
    }

    public void setColumn3(String column3) {
        this.column3 = column3;
    }

    public String getColumn4() {
        return column4;
    }

    public void setColumn4(String column4) {
        this.column4 = column4;
    }

    public String getColumn5() {
        return column5;
    }

    public void setColumn5(String column5) {
        this.column5 = column5;
    }

    public String getColumn6() {
        return column6;
    }

    public void setColumn6(String column6) {
        this.column6 = column6;
    }

    public String getColumn7() {
        return column7;
    }

    public void setColumn7(String column7) {
        this.column7 = column7;
    }

    public String getColumn8() {
        return column8;
    }

    public void setColumn8(String column8) {
        this.column8 = column8;
    }

    public String getColumn9() {
        return column9;
    }

    public void setColumn9(String column9) {
        this.column9 = column9;
    }

    public String getColumn11() {
        return column11;
    }

    public void setColumn11(String column11) {
        this.column11 = column11;
    }

    public String getColumn12() {
        return column12;
    }

    public void setColumn12(String column12) {
        this.column12 = column12;
    }

    public String getColumn13() {
        return column13;
    }

    public void setColumn13(String column13) {
        this.column13 = column13;
    }

    public String getColumn14() {
        return column14;
    }

    public void setColumn14(String column14) {
        this.column14 = column14;
    }

    public String getColumn15() {
        return column15;
    }

    public void setColumn15(String column15) {
        this.column15 = column15;
    }

    public String getColumn16() {
        return column16;
    }

    public void setColumn16(String column16) {
        this.column16 = column16;
    }

    public String getColumn17() {
        return column17;
    }

    public void setColumn17(String column17) {
        this.column17 = column17;
    }

    public String getColumn18() {
        return column18;
    }

    public void setColumn18(String column18) {
        this.column18 = column18;
    }

    public String getColumn19() {
        return column19;
    }

    public void setColumn19(String column19) {
        this.column19 = column19;
    }

    public String getColumn20() {
        return column20;
    }

    public void setColumn20(String column20) {
        this.column20 = column20;
    }

    public String getColumn21() {
        return column21;
    }

    public void setColumn21(String column21) {
        this.column21 = column21;
    }

    public String getColumn22() {
        return column22;
    }

    public void setColumn22(String column22) {
        this.column22 = column22;
    }

    public String getColumn23() {
        return column23;
    }

    public void setColumn23(String column23) {
        this.column23 = column23;
    }

    public String getColumn24() {
        return column24;
    }

    public void setColumn24(String column24) {
        this.column24 = column24;
    }

    public String getColumn25() {
        return column25;
    }

    public void setColumn25(String column25) {
        this.column25 = column25;
    }

    public String getColumn26() {
        return column26;
    }

    public void setColumn26(String column26) {
        this.column26 = column26;
    }

    public String getColumn27() {
        return column27;
    }

    public void setColumn27(String column27) {
        this.column27 = column27;
    }

    public String getColumn28() {
        return column28;
    }

    public void setColumn28(String column28) {
        this.column28 = column28;
    }

    public String getColumn29() {
        return column29;
    }

    public void setColumn29(String column29) {
        this.column29 = column29;
    }

    public String getColumn30() {
        return column30;
    }

    public void setColumn30(String column30) {
        this.column30 = column30;
    }

    public String getColumn31() {
        return column31;
    }

    public void setColumn31(String column31) {
        this.column31 = column31;
    }

    public String getColumn32() {
        return column32;
    }

    public void setColumn32(String column32) {
        this.column32 = column32;
    }

    public String getColumn33() {
        return column33;
    }

    public void setColumn33(String column33) {
        this.column33 = column33;
    }

    public String getColumn34() {
        return column34;
    }

    public void setColumn34(String column34) {
        this.column34 = column34;
    }

    public String getColumn35() {
        return column35;
    }

    public void setColumn35(String column35) {
        this.column35 = column35;
    }

    public String getColumn36() {
        return column36;
    }

    public void setColumn36(String column36) {
        this.column36 = column36;
    }

    public String getColumn37() {
        return column37;
    }

    public void setColumn37(String column37) {
        this.column37 = column37;
    }

    public String getColumn38() {
        return column38;
    }

    public void setColumn38(String column38) {
        this.column38 = column38;
    }

    public String getColumn39() {
        return column39;
    }

    public void setColumn39(String column39) {
        this.column39 = column39;
    }

    public String getColumn40() {
        return column40;
    }

    public void setColumn40(String column40) {
        this.column40 = column40;
    }

    public String getColumn41() {
        return column41;
    }

    public void setColumn41(String column41) {
        this.column41 = column41;
    }

    public String getColumn42() {
        return column42;
    }

    public void setColumn42(String column42) {
        this.column42 = column42;
    }

    public String getColumn43() {
        return column43;
    }

    public void setColumn43(String column43) {
        this.column43 = column43;
    }

    public String getColumn44() {
        return column44;
    }

    public void setColumn44(String column44) {
        this.column44 = column44;
    }

    public String getColumn45() {
        return column45;
    }

    public void setColumn45(String column45) {
        this.column45 = column45;
    }

    public String getColumn46() {
        return column46;
    }

    public void setColumn46(String column46) {
        this.column46 = column46;
    }

    public String getColumn47() {
        return column47;
    }

    public void setColumn47(String column47) {
        this.column47 = column47;
    }

    public String getColumn48() {
        return column48;
    }

    public void setColumn48(String column48) {
        this.column48 = column48;
    }

    public String getColumn49() {
        return column49;
    }

    public void setColumn49(String column49) {
        this.column49 = column49;
    }

    public String getColumn50() {
        return column50;
    }

    public void setColumn50(String column50) {
        this.column50 = column50;
    }

    public String getColumn51() {
        return column51;
    }

    public void setColumn51(String column51) {
        this.column51 = column51;
    }

    public String getColumn52() {
        return column52;
    }

    public void setColumn52(String column52) {
        this.column52 = column52;
    }

    public String getColumn53() {
        return column53;
    }

    public void setColumn53(String column53) {
        this.column53 = column53;
    }

    public String getColumn54() {
        return column54;
    }

    public void setColumn54(String column54) {
        this.column54 = column54;
    }

    public String getColumn55() {
        return column55;
    }

    public void setColumn55(String column55) {
        this.column55 = column55;
    }

    public String getColumn56() {
        return column56;
    }

    public void setColumn56(String column56) {
        this.column56 = column56;
    }

    public String getColumn57() {
        return column57;
    }

    public void setColumn57(String column57) {
        this.column57 = column57;
    }

    public String getColumn58() {
        return column58;
    }

    public void setColumn58(String column58) {
        this.column58 = column58;
    }

    public String getColumn59() {
        return column59;
    }

    public void setColumn59(String column59) {
        this.column59 = column59;
    }

    public String getColumn60() {
        return column60;
    }

    public void setColumn60(String column60) {
        this.column60 = column60;
    }

    public String getColumn100() {
        return column100;
    }

    public void setColumn100(String column100) {
        this.column100 = column100;
    }

    public String getColumn101() {
        return column101;
    }

    public void setColumn101(String column101) {
        this.column101 = column101;
    }

    public String getColumn102() {
        return column102;
    }

    public void setColumn102(String column102) {
        this.column102 = column102;
    }

    public String getColumn103() {
        return column103;
    }

    public void setColumn103(String column103) {
        this.column103 = column103;
    }

    public String getColumn104() {
        return column104;
    }

    public void setColumn104(String column104) {
        this.column104 = column104;
    }

    public String getColumn105() {
        return column105;
    }

    public void setColumn105(String column105) {
        this.column105 = column105;
    }

    public String getColumn106() {
        return column106;
    }

    public void setColumn106(String column106) {
        this.column106 = column106;
    }

    public String getColumn107() {
        return column107;
    }

    public void setColumn107(String column107) {
        this.column107 = column107;
    }

    public String getColumn108() {
        return column108;
    }

    public void setColumn108(String column108) {
        this.column108 = column108;
    }

    public String getColumn109() {
        return column109;
    }

    public void setColumn109(String column109) {
        this.column109 = column109;
    }

    public String getColumn110() {
        return column110;
    }

    public void setColumn110(String column110) {
        this.column110 = column110;
    }

    public String getColumn111() {
        return column111;
    }

    public void setColumn111(String column111) {
        this.column111 = column111;
    }

    public String getColumn112() {
        return column112;
    }

    public void setColumn112(String column112) {
        this.column112 = column112;
    }

    public String getColumn113() {
        return column113;
    }

    public void setColumn113(String column113) {
        this.column113 = column113;
    }

    public String getColumn114() {
        return column114;
    }

    public void setColumn114(String column114) {
        this.column114 = column114;
    }

    public String getColumn115() {
        return column115;
    }

    public void setColumn115(String column115) {
        this.column115 = column115;
    }

    public String getColumn116() {
        return column116;
    }

    public void setColumn116(String column116) {
        this.column116 = column116;
    }

    public String getColumn117() {
        return column117;
    }

    public void setColumn117(String column117) {
        this.column117 = column117;
    }

    public String getColumn118() {
        return column118;
    }

    public void setColumn118(String column118) {
        this.column118 = column118;
    }

    public String getColumn119() {
        return column119;
    }

    public void setColumn119(String column119) {
        this.column119 = column119;
    }

    public String getColumn120() {
        return column120;
    }

    public void setColumn120(String column120) {
        this.column120 = column120;
    }

    public String getColumn121() {
        return column121;
    }

    public void setColumn121(String column121) {
        this.column121 = column121;
    }

    public String getColumn122() {
        return column122;
    }

    public void setColumn122(String column122) {
        this.column122 = column122;
    }

    public String getColumn123() {
        return column123;
    }

    public void setColumn123(String column123) {
        this.column123 = column123;
    }

    public String getColumn124() {
        return column124;
    }

    public void setColumn124(String column124) {
        this.column124 = column124;
    }

    public String getColumn125() {
        return column125;
    }

    public void setColumn125(String column125) {
        this.column125 = column125;
    }

    public String getColumn126() {
        return column126;
    }

    public void setColumn126(String column126) {
        this.column126 = column126;
    }

    public String getColumn127() {
        return column127;
    }

    public void setColumn127(String column127) {
        this.column127 = column127;
    }

    public String getColumn128() {
        return column128;
    }

    public void setColumn128(String column128) {
        this.column128 = column128;
    }

    public String getColumn129() {
        return column129;
    }

    public void setColumn129(String column129) {
        this.column129 = column129;
    }

    public String getColumn130() {
        return column130;
    }

    public void setColumn130(String column130) {
        this.column130 = column130;
    }

    public String getColumn61() {
        return column61;
    }

    public void setColumn61(String column61) {
        this.column61 = column61;
    }

    public String getColumn62() {
        return column62;
    }

    public void setColumn62(String column62) {
        this.column62 = column62;
    }

    public String getColumn63() {
        return column63;
    }

    public void setColumn63(String column63) {
        this.column63 = column63;
    }

    public String getColumn64() {
        return column64;
    }

    public void setColumn64(String column64) {
        this.column64 = column64;
    }

    public String getColumn65() {
        return column65;
    }

    public void setColumn65(String column65) {
        this.column65 = column65;
    }

    public String getColumn66() {
        return column66;
    }

    public void setColumn66(String column66) {
        this.column66 = column66;
    }

    public String getColumn67() {
        return column67;
    }

    public void setColumn67(String column67) {
        this.column67 = column67;
    }

    public String getColumn68() {
        return column68;
    }

    public void setColumn68(String column68) {
        this.column68 = column68;
    }

    public String getColumn69() {
        return column69;
    }

    public void setColumn69(String column69) {
        this.column69 = column69;
    }

    public String getColumn70() {
        return column70;
    }

    public void setColumn70(String column70) {
        this.column70 = column70;
    }

    public String getColumn71() {
        return column71;
    }

    public void setColumn71(String column71) {
        this.column71 = column71;
    }

    public String getColumn72() {
        return column72;
    }

    public void setColumn72(String column72) {
        this.column72 = column72;
    }

    public String getColumn73() {
        return column73;
    }

    public void setColumn73(String column73) {
        this.column73 = column73;
    }

    public String getColumn74() {
        return column74;
    }

    public void setColumn74(String column74) {
        this.column74 = column74;
    }

    public String getColumn75() {
        return column75;
    }

    public void setColumn75(String column75) {
        this.column75 = column75;
    }

    public String getColumn76() {
        return column76;
    }

    public void setColumn76(String column76) {
        this.column76 = column76;
    }

    public String getColumn77() {
        return column77;
    }

    public void setColumn77(String column77) {
        this.column77 = column77;
    }

    public String getColumn78() {
        return column78;
    }

    public void setColumn78(String column78) {
        this.column78 = column78;
    }

    public String getColumn79() {
        return column79;
    }

    public void setColumn79(String column79) {
        this.column79 = column79;
    }

    public String getColumn80() {
        return column80;
    }

    public void setColumn80(String column80) {
        this.column80 = column80;
    }

    public String getColumn81() {
        return column81;
    }

    public void setColumn81(String column81) {
        this.column81 = column81;
    }

    public String getColumn82() {
        return column82;
    }

    public void setColumn82(String column82) {
        this.column82 = column82;
    }

    public String getColumn83() {
        return column83;
    }

    public void setColumn83(String column83) {
        this.column83 = column83;
    }

    public String getColumn84() {
        return column84;
    }

    public void setColumn84(String column84) {
        this.column84 = column84;
    }

    public String getColumn85() {
        return column85;
    }

    public void setColumn85(String column85) {
        this.column85 = column85;
    }

    public String getColumn86() {
        return column86;
    }

    public void setColumn86(String column86) {
        this.column86 = column86;
    }

    public String getColumn87() {
        return column87;
    }

    public void setColumn87(String column87) {
        this.column87 = column87;
    }

    public String getColumn88() {
        return column88;
    }

    public void setColumn88(String column88) {
        this.column88 = column88;
    }

    public String getColumn89() {
        return column89;
    }

    public void setColumn89(String column89) {
        this.column89 = column89;
    }

    public String getColumn90() {
        return column90;
    }

    public void setColumn90(String column90) {
        this.column90 = column90;
    }

    public String getColumn91() {
        return column91;
    }

    public void setColumn91(String column91) {
        this.column91 = column91;
    }

    public String getColumn92() {
        return column92;
    }

    public void setColumn92(String column92) {
        this.column92 = column92;
    }

    public String getColumn93() {
        return column93;
    }

    public void setColumn93(String column93) {
        this.column93 = column93;
    }

    public String getColumn94() {
        return column94;
    }

    public void setColumn94(String column94) {
        this.column94 = column94;
    }

    public String getColumn95() {
        return column95;
    }

    public void setColumn95(String column95) {
        this.column95 = column95;
    }

    public String getColumn96() {
        return column96;
    }

    public void setColumn96(String column96) {
        this.column96 = column96;
    }

    public String getColumn97() {
        return column97;
    }

    public void setColumn97(String column97) {
        this.column97 = column97;
    }

    public String getColumn98() {
        return column98;
    }

    public void setColumn98(String column98) {
        this.column98 = column98;
    }

    public String getColumn99() {
        return column99;
    }

    public void setColumn99(String column99) {
        this.column99 = column99;
    }
}
