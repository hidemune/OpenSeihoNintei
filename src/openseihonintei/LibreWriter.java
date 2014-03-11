package openseihonintei;


import com.sun.star.awt.Size;
import com.sun.star.awt.XBitmap;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.container.XNameContainer;
import com.sun.star.document.XDocumentInsertable;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XFrame;
import com.sun.star.graphic.XGraphic;
import com.sun.star.graphic.XGraphicProvider;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.style.BreakType;
import com.sun.star.table.XCell;
import com.sun.star.text.HoriOrientation;
import com.sun.star.text.VertOrientation;
import com.sun.star.text.WrapTextMode;
import com.sun.star.text.XText;
import com.sun.star.text.XTextContent;
import com.sun.star.text.XTextCursor;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextRange;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import ooo.connector.BootstrapSocketConnector;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author hdm
 */
public class LibreWriter {
public static String LibreExePath = "C:\\Program Files (x86)\\LibreOffice 4\\program";
com.sun.star.uno.XComponentContext makeContext = null;
com.sun.star.frame.XDesktop makeDesktop = null;

    public void makeWiterFile(ArrayList<String> ArrStr[]) {
        System.out.println("LibreExePath:" + LibreExePath);
        if (LibreExePath.equals("")) {
            //Msg
            JOptionPane.showMessageDialog(null, "LibreOfficeの実行ファイルのあるフォルダが設定されていません。処理を中断します。");
            return;
        }
        if (JOptionPane.showConfirmDialog(null, "LibreOffice文書を作成します。\nよろしいですか？") != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            //com.sun.star.frame.XDesktop xDesktop = null;
            makeDesktop = getDesktop();
            
            com.sun.star.text.XTextDocument xTextDocument =
                //createTextdocument( xDesktop );
                    openCreatedocument(makeDesktop);
            //append(xTextDocument, ArrStr);
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
            return;
        }
        JOptionPane.showMessageDialog(null, "処理が完了しました。\n目次を右クリックして「目次と索引の更新」を実行してください。\nその後、名前をつけて保存してください。");
        System.out.println("Done");
    }
    
    
    public com.sun.star.text.XTextDocument openCreatedocument(
        com.sun.star.frame.XDesktop xDesktop)
    {
        com.sun.star.text.XTextDocument aTextDocument = null;
        
        
            try {
                // get the remote office component context
                String oooExeFolder = LibreExePath;
                makeContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                //makeContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
                System.out.println("Connected to a running office ...");

                // get the remote office service manager
                com.sun.star.lang.XMultiComponentFactory xMCF =
                    makeContext.getServiceManager();

                Object oDesktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", makeContext);

                com.sun.star.frame.XComponentLoader xCompLoader =
                    (com.sun.star.frame.XComponentLoader)
                         UnoRuntime.queryInterface(
                             com.sun.star.frame.XComponentLoader.class, oDesktop);

                com.sun.star.beans.PropertyValue[] propertyValue =
                    new com.sun.star.beans.PropertyValue[1];
                propertyValue[0] = new com.sun.star.beans.PropertyValue();
                propertyValue[0].Name = "AsTemplate";
                propertyValue[0].Value = true;
                //propertyValue[1] = new com.sun.star.beans.PropertyValue();
                //propertyValue[1].Name = "Hidden";
                //propertyValue[1].Value = true;
                java.io.File sourceFile = new java.io.File("print/chosyo2.odt");
                StringBuilder sLoadUrl = new StringBuilder("file:///");
                sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                Object oDocToStore = xCompLoader.loadComponentFromURL(
                    sLoadUrl.toString(), "_blank", 0, propertyValue );
                
                aTextDocument = (com.sun.star.text.XTextDocument)
                    UnoRuntime.queryInterface(
                        com.sun.star.text.XTextDocument.class, oDocToStore);
                
                //Calc
                XSpreadsheetDocument myDoc = (XSpreadsheetDocument)UnoRuntime.queryInterface( XSpreadsheetDocument.class, oDocToStore);
                XSpreadsheets xSheets = myDoc.getSheets() ;
                XSpreadsheet xSheet = null;
                XCell xCell = null;
                
                try {
                    xSheet = (XSpreadsheet) xSheets.getByName("調書2");
                    xCell = (XCell) xSheet.getCellRangeByName("CaseNo");
                } catch (Exception ex) {
                    System.err.println("Could not get Cell");
                    ex.printStackTrace(System.err);
                }
                
                //if (flag.equals("V")) {
                //    xCell.setValue((new Float(theValue)).floatValue());
                //} else {
                    xCell.setFormula("aaaa-12345678");
                //}
                /*
                //目次ページのみ改ページ
                com.sun.star.text.XTextCursor xTextCursor = null;
                xTextCursor = (com.sun.star.text.XTextCursor) aTextDocument.getText().createTextCursor();
                xTextCursor.gotoEnd(false);
                XPropertySet cursorProperty = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xTextCursor); 
                cursorProperty.setPropertyValue("BreakType", BreakType.PAGE_BEFORE);
                aTextDocument.getText().insertString( xTextCursor, "test\n" , true );
                */
            }catch (Exception e) {
                Logger.getLogger(LibreWriter.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\n「print/chosyo2.odt」がカレントディレクトリにあるか確認してください。");
            }
        
        return aTextDocument;
    }
    
    public com.sun.star.text.XTextDocument createTextdocument(
        com.sun.star.frame.XDesktop xDesktop )
    {
        com.sun.star.text.XTextDocument aTextDocument = null;

        try {
            com.sun.star.lang.XComponent xComponent = CreateNewDocument(xDesktop,
                                                                        "swriter");
            aTextDocument = (com.sun.star.text.XTextDocument)
                UnoRuntime.queryInterface(
                    com.sun.star.text.XTextDocument.class, xComponent);
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
        }

        return aTextDocument;
    }
    protected com.sun.star.lang.XComponent CreateNewDocument(
        com.sun.star.frame.XDesktop xDesktop,
        String sDocumentType )
    {
        String sURL = "private:factory/" + sDocumentType;

        com.sun.star.lang.XComponent xComponent = null;
        com.sun.star.frame.XComponentLoader xComponentLoader = null;
        com.sun.star.beans.PropertyValue xValues[] =
            new com.sun.star.beans.PropertyValue[1];
        com.sun.star.beans.PropertyValue xEmptyArgs[] =
            new com.sun.star.beans.PropertyValue[0];

        try {
            xComponentLoader = (com.sun.star.frame.XComponentLoader)
                UnoRuntime.queryInterface(
                    com.sun.star.frame.XComponentLoader.class, xDesktop);

            xComponent  = xComponentLoader.loadComponentFromURL(
                sURL, "_blank", 0, xEmptyArgs);
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
        }

        return xComponent ;
    }
    
    public void append(XTextDocument xTextDocumentMake, ArrayList<String> ArrStr[]) throws IllegalArgumentException, Exception {
        com.sun.star.uno.XComponentContext xContext = null;
        String mMotoWords[] = {"&&TITLE&&", "&&COL1&&", "&&COL2&&", "&&COL3&&", "&&COL4&&", "&&COL5&&", "&&TEXT&&", "&&INDEX&&"};
        //環境設定画面の取得
        String[] strConf = {"","","","","","","","","",""};
        //card.ConfFrm.getText(strConf);
        String title = strConf[1];
        
        for (int Idx = 0; Idx < ArrStr[0].size(); Idx++) {
            String m1maeWords[] = {"","","","","","","","","","","","","","","","","","","","","","","","","","","","","",""};
            if (0 < Idx) {
                m1maeWords[0] = ""; //TITLE は毎回変換
                m1maeWords[1] = ArrStr[0].get(Idx - 1);
                m1maeWords[2] = ArrStr[1].get(Idx - 1);
                m1maeWords[3] = ArrStr[2].get(Idx - 1);
                m1maeWords[4] = ArrStr[3].get(Idx - 1);
                m1maeWords[5] = "";  //ファイル名
                m1maeWords[6] = ArrStr[5].get(Idx - 1);
                m1maeWords[7] = String.valueOf(Idx + 1 - 1);
            }
            String mSakiWords[] = { title, ArrStr[0].get(Idx), ArrStr[1].get(Idx), ArrStr[2].get(Idx), ArrStr[3].get(Idx), "", ArrStr[5].get(Idx), String.valueOf(Idx + 1) };
            
            try {
                //テンプレートファイル
                // get the remote office component context
                String oooExeFolder = LibreExePath;
                xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                //xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
                System.out.println("Connected to a running office ...");

                // get the remote office service manager
                com.sun.star.lang.XMultiComponentFactory xMCF =
                    xContext.getServiceManager();
                Object oDesktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", xContext);
                
                com.sun.star.frame.XComponentLoader xCompLoader =
                    (com.sun.star.frame.XComponentLoader)
                         UnoRuntime.queryInterface(
                             com.sun.star.frame.XComponentLoader.class, oDesktop);
                

                //テンプレート読み込み
                com.sun.star.beans.PropertyValue[] propertyValue =
                    new com.sun.star.beans.PropertyValue[1];
                propertyValue[0] = new com.sun.star.beans.PropertyValue();
                propertyValue[0].Name = "Hidden";
                propertyValue[0].Value = true;
                java.io.File sourceFile = new java.io.File("print/chosyo2.odt");
                StringBuilder sLoadUrl = new StringBuilder("file:///");
                sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                Object oDocToStore = xCompLoader.loadComponentFromURL(
                    sLoadUrl.toString(), "_blank", 0, propertyValue );

                com.sun.star.text.XTextDocument aTextDocument = (com.sun.star.text.XTextDocument)
                    UnoRuntime.queryInterface(
                        com.sun.star.text.XTextDocument.class, oDocToStore);

                //文字列変換
                com.sun.star.util.XReplaceDescriptor xReplaceDescr = null;
                com.sun.star.util.XReplaceable xReplaceable = null;

                xReplaceable = (com.sun.star.util.XReplaceable)
                    UnoRuntime.queryInterface(
                        com.sun.star.util.XReplaceable.class, aTextDocument);

                // You need a descriptor to set properies for Replace
                xReplaceDescr = (com.sun.star.util.XReplaceDescriptor)
                    xReplaceable.createReplaceDescriptor();

                System.out.println("Change all occurrences of ...");
                for( int iArrayCounter = 0; iArrayCounter < mMotoWords.length;
                     iArrayCounter++ )
                {
                    //System.out.println(mMotoWords[iArrayCounter] +
                    //    " -> " + mSakiWords[iArrayCounter]);
                    // Set the properties the replace method need
                    xReplaceDescr.setSearchString(mMotoWords[iArrayCounter]);
                    //xReplaceDescr.setReplaceString(mSakiWords[iArrayCounter]);
                    String strSaki = mSakiWords[iArrayCounter];
                    //前と同じなら非表示
                    boolean isOnaji = false;    //TITLE用
                    if (iArrayCounter > 0) {
                        isOnaji = true;     //見出し判定用初期値
                        for (int i = 1; i < iArrayCounter + 1; i++) {
                            if (!mSakiWords[i].equals(m1maeWords[i])) {
                                //前まで含めて全部同じかどうか
                                isOnaji = false;
                                //System.err.println(mSakiWords[i] + "!=" + m1maeWords[i]);
                            }
                        }
                    }
                    //前まで含めて１個でも違えば表示
                    if (isOnaji) {
                        strSaki = "";
                    }
                    // Replace all words
                    Object firstObject = xReplaceable.findFirst(xReplaceDescr);
                    //System.out.println("firstObject:" + firstObject);
                    if (firstObject != null) {
                        //文字列置換
                        XTextRange xtr = (XTextRange) UnoRuntime.queryInterface(XTextRange.class, firstObject);
                        //System.out.println("xtr:" + xtr);
                        
                        if (strSaki.equals("")) {
                            xtr.setString(strSaki);
                            //同じなら行そのものを消す
                            com.sun.star.text.XTextCursor xTextCursor = null;
                            xTextCursor = xtr.getText().createTextCursorByRange(xtr); 
                            xTextCursor.goLeft(Short.valueOf("1"), true);
                            xTextCursor.setString("");
                        } else {
                            //COL1-COL5なら「番号継続」
                            if ((1 <= iArrayCounter) && (iArrayCounter <= 5)) {
                                strSaki = strSaki + "&&MIDASI&&";
                            }
                            xtr.setString(strSaki);
                        }
                        /* else {             テンポラリでやっても無意味
                            //COL1-COL5なら「番号継続」
                            if ((1 <= iArrayCounter) && (iArrayCounter <= 5)) {
                                propertyValue = new com.sun.star.beans.PropertyValue[ 0 ];
                                xDispatchHelper.executeDispatch(xDispatchProvider, ".uno:ContinueNumbering", "", 0, propertyValue);
                                System.err.println("見出し：" + strSaki);
                            }
                        } */
                    }
                }

                com.sun.star.frame.XStorable xStorable =
                    (com.sun.star.frame.XStorable)UnoRuntime.queryInterface(
                        com.sun.star.frame.XStorable.class, oDocToStore );
                sourceFile = new java.io.File("tmp" + Idx+ ".odt");
                StringBuilder sSaveUrl = new StringBuilder("file:///");
                sSaveUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                // save
                propertyValue = new com.sun.star.beans.PropertyValue[ 2 ];
                propertyValue[0] = new com.sun.star.beans.PropertyValue();
                propertyValue[0].Name = "Overwrite";
                propertyValue[0].Value = new Boolean(true);
                propertyValue[1] = new com.sun.star.beans.PropertyValue();
                propertyValue[1].Name = "FilterName";
                propertyValue[1].Value = "writer8";
                xStorable.storeAsURL( sSaveUrl.toString(), propertyValue );
                
                com.sun.star.util.XCloseable xCloseable = (com.sun.star.util.XCloseable)
                    UnoRuntime.queryInterface(com.sun.star.util.XCloseable.class,
                                              oDocToStore );
                if (xCloseable != null ) {
                    xCloseable.close(false);
                } else {
                    com.sun.star.lang.XComponent xComp = (com.sun.star.lang.XComponent)
                        UnoRuntime.queryInterface(
                            com.sun.star.lang.XComponent.class, oDocToStore );
                    xComp.dispose();
                }
                
                com.sun.star.text.XTextCursor xTextCursor = null;
                xTextCursor = xTextDocumentMake.getText().createTextCursor();
                xTextCursor.gotoEnd(false);
                XPropertySet cursorProperty = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class, xTextCursor); 
                if (Idx == 0) { //１ページ目のみ改ページ
                    cursorProperty.setPropertyValue("BreakType", BreakType.PAGE_BEFORE);
                }else {
                    cursorProperty.setPropertyValue("BreakType", BreakType.NONE);
                }
                XDocumentInsertable xDocI = (XDocumentInsertable) UnoRuntime.queryInterface(XDocumentInsertable.class, xTextCursor);            
                xDocI.insertDocumentFromURL(sSaveUrl.toString(), new PropertyValue[0]);

            } catch (com.sun.star.lang.IllegalArgumentException ex) {
                Logger.getLogger(LibreWriter.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\nカレントディレクトリに「template/template.odt」があるか確認してください。");
                throw ex;
                //return;
            } catch (Exception ex) {
                Logger.getLogger(LibreWriter.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。");
                throw ex;
                //return;
            }
        }   //全行ループ終了

        com.sun.star.util.XReplaceDescriptor xReplaceDescr = null;
        com.sun.star.util.XReplaceable xReplaceable = null;
        com.sun.star.text.XText xTextMake = xTextDocumentMake.getText();
        XTextCursor xTextCursor = xTextMake.createTextCursor();
        xTextCursor.gotoStart(false);
        xReplaceable = (com.sun.star.util.XReplaceable)
            UnoRuntime.queryInterface(
                com.sun.star.util.XReplaceable.class, xTextDocumentMake);
        xReplaceDescr = (com.sun.star.util.XReplaceDescriptor)
            xReplaceable.createReplaceDescriptor();
        xReplaceDescr.setSearchString("&&MIDASI&&");
        
        XController xController = xTextDocumentMake.getCurrentController();
        XFrame xFrame = xController.getFrame(); 
        XTextViewCursorSupplier xViewCursorSupplier = (XTextViewCursorSupplier)UnoRuntime.queryInterface(XTextViewCursorSupplier.class, xController); 
        XTextViewCursor xViewCursor = xViewCursorSupplier.getViewCursor(); 
        
        
        // get the remote office service manager
        com.sun.star.lang.XMultiComponentFactory xMCF =
            makeContext.getServiceManager();
        XMultiComponentFactory xRemoteServiceManager = null;
        //XURLTransformer xTransformer = null;
        xRemoteServiceManager = makeContext.getServiceManager();

        //コマンドを発行可能にする
        Object configProvider = xRemoteServiceManager.createInstanceWithContext(
                      "com.sun.star.configuration.ConfigurationProvider",
                      makeContext );
        XMultiServiceFactory xConfigProvider = null;
        xConfigProvider = (com.sun.star.lang.XMultiServiceFactory)
            UnoRuntime.queryInterface(
                com.sun.star.lang.XMultiServiceFactory.class, configProvider );
        enableCommands(xConfigProvider);
        
        //Object transformer = xRemoteServiceManager.createInstanceWithContext(
        //              "com.sun.star.util.URLTransformer", makeContext );
        //xTransformer = (com.sun.star.util.XURLTransformer)
        //    UnoRuntime.queryInterface(com.sun.star.util.XURLTransformer.class,
        //                              transformer );
        
        com.sun.star.frame.XDispatchProvider xDispatchProvider =
             (com.sun.star.frame.XDispatchProvider)UnoRuntime.queryInterface(
                 com.sun.star.frame.XDispatchProvider.class, xFrame );
        XMultiServiceFactory xMSF = (XMultiServiceFactory) UnoRuntime.queryInterface(XMultiServiceFactory.class, xMCF);
        Object sDispatchHelper= xMSF.createInstance("com.sun.star.frame.DispatchHelper");
        XDispatchHelper xDispatchHelper=(XDispatchHelper)
                UnoRuntime.queryInterface(XDispatchHelper.class, sDispatchHelper);
        com.sun.star.beans.PropertyValue[] propertyValue =
            new com.sun.star.beans.PropertyValue[1];
        propertyValue = new com.sun.star.beans.PropertyValue[ 0 ];
        
        boolean flg = false;
        while (true) {
            Object firstObject = xReplaceable.findFirst(xReplaceDescr);
            if (firstObject == null) {
                break;
            }else {
                //文字列置換
                //カーソルを移動しないと番号継続できない
                XTextRange xtr = (XTextRange) UnoRuntime.queryInterface(XTextRange.class, firstObject);
                xtr.setString("");
                xViewCursor.gotoRange(xtr, false); 
                //COL1-COL5なら「番号継続」
                if (flg) {
                    xDispatchHelper.executeDispatch(xDispatchProvider, ".uno:ContinueNumbering", "", 0, propertyValue);
                }
                flg = true;
            }
        }
        xViewCursor.gotoStart(false);
        
        //画像取り込み
        // Querying for the interface XMultiServiceFactory on the xtextdocument
        com.sun.star.lang.XMultiServiceFactory xMSFDoc =
                (com.sun.star.lang.XMultiServiceFactory)UnoRuntime.queryInterface(
                    com.sun.star.lang.XMultiServiceFactory.class, xTextDocumentMake);
        // Getting the text
        //
        xReplaceDescr.setSearchString("&&IMAGE&&");
        while (true) {
            Object firstObject = xReplaceable.findFirst(xReplaceDescr);
            if (firstObject == null) {
                break;
            }else {
                //文字列置換
                //カーソルを移動
                XTextRange xtr = (XTextRange) UnoRuntime.queryInterface(XTextRange.class, firstObject);
                xtr.setString("");
                xViewCursor.gotoRange(xtr, false); 
                //行末まで選択・ファイル名取得
                xDispatchHelper.executeDispatch(xDispatchProvider, ".uno:EndOfLineSel", "", 0, new PropertyValue[0]);
                String imgfilename = xViewCursor.getString();
                System.out.println("イメージファイル名：" + imgfilename);
                
                //ファイル名取得したら行そのものを消す
                xViewCursor.setString("");  //選択済のファイル名を消す
                //xTextCursor = xtr.getText().createTextCursorByRange(xtr); 
                //xViewCursor.goLeft(Short.valueOf("1"), true);
                //xViewCursor.setString("");  //１行削除
                
                
                //insert a paragraph break これをしないと段落の一番上に画像が来てしまう
                try {
                    xTextMake.insertControlCharacter(xViewCursor,
                              com.sun.star.text.ControlCharacter.PARAGRAPH_BREAK, false );
                } catch (Exception e) {
                    System.err.println("Couldn't insert break "+ e);
                    e.printStackTrace(System.err);
                }
                
                
                // Creating a string for the graphic url
                java.io.File sourceFile = new java.io.File(imgfilename);
                StringBuffer sUrl = new StringBuffer("file:///");
                sUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                System.out.println( "insert graphic \"" + sUrl + "\"");
                //画像読み込み（埋め込み）
                embedGraphic(xContext,xMCF,xMSFDoc,xViewCursor,sUrl.toString());
                
                /*
                //Graphic 読み込み（リンク）
                Object oGraphic = xMSFDoc.createInstance("com.sun.star.text.TextGraphicObject");
                
                // Querying for the interface XTextContent on the GraphicObject
                com.sun.star.text.XTextContent xTextContent =
                    (com.sun.star.text.XTextContent)UnoRuntime.queryInterface(
                        com.sun.star.text.XTextContent.class, oGraphic );
                
                // Printing information to the log file
                System.out.println( "inserting graphic" );
                try {
                    // Inserting the content
                    xTextMake.insertTextContent(xViewCursor, xTextContent, false);
                } catch ( Exception exception ) {
                    System.out.println( "Could not insert Content" );
                    exception.printStackTrace(System.err);
                }
                
                // Printing information to the log file
                System.out.println( "adding graphic" );

                // Querying for the interface XPropertySet on GraphicObject
                
                com.sun.star.beans.XPropertySet xPropSet =
                    (com.sun.star.beans.XPropertySet)UnoRuntime.queryInterface(
                        com.sun.star.beans.XPropertySet.class, oGraphic);
                try {
                    // Creating a string for the graphic url
                    java.io.File sourceFile = new java.io.File(imgfilename);
                    StringBuffer sUrl = new StringBuffer("file:///");
                    sUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                    System.out.println( "insert graphic \"" + sUrl + "\"");

                    // Setting the anchor type
                    xPropSet.setPropertyValue("AnchorType",
                               com.sun.star.text.TextContentAnchorType.AT_PARAGRAPH );  //AT_PARAGRAPH じゃないとうまく行かない

                    // Setting the graphic url
                    xPropSet.setPropertyValue( "GraphicURL", sUrl.toString() );

                    xPropSet.setPropertyValue("TextWrap", WrapTextMode.NONE ); 
                    xPropSet.setPropertyValue("HoriOrient", HoriOrientation.LEFT); 
                    xPropSet.setPropertyValue("VertOrient", VertOrientation.NONE); 
                    
                } catch ( Exception exception ) {
                    System.out.println( "Couldn't set property 'GraphicURL'" );
                    exception.printStackTrace();
                }
*/
                xContext = null;
                
            }
            xViewCursor.gotoStart(false);
        }
        
        //tmpファイルの削除
        for (int i = 0; i < ArrStr[0].size(); i++) {
            File file = new File("tmp" + i + ".odt");
            if (file.exists()){
                file.delete();
            }
        }
    }
    
    /**
     * Embeds the license "button" into a Textdocument at the given cursor position
     *
     * @param xMSF    the factory to create services from
     * @param xCursor the cursor where to insert the graphic
     * @param imgURL  URL of the license button
     *
     */
    private static void embedGraphic(com.sun.star.uno.XComponentContext xContext,com.sun.star.lang.XMultiComponentFactory xMCF,XMultiServiceFactory mxDocFactory, XTextCursor xCursor, String imgURL) {
       /* XMultiServiceFactory mxDocFactory = null;
        XTextDocument mxTextDoc = null;*/
        XNameContainer xBitmapContainer = null;
        XText xText = xCursor.getText();
        XTextContent xImage = null;
        String internalURL = null;
        String imgID = "tempImageId" + System.currentTimeMillis();
        
        try {
            
            // query its XTextDocument interface to get the text
           /* mxTextDoc = (XTextDocument)UnoRuntime.queryInterface(
                    XTextDocument.class, xTextComponent);
            
              mxDocFactory = (XMultiServiceFactory)UnoRuntime.queryInterface(
                    XMultiServiceFactory.class, mxTextDoc);*/
            
            xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                    XNameContainer.class, mxDocFactory.createInstance(
                    "com.sun.star.drawing.BitmapTable"));
            xImage = (XTextContent) UnoRuntime.queryInterface(
                    XTextContent.class,     mxDocFactory.createInstance(
                    "com.sun.star.text.TextGraphicObject"));
            XPropertySet xProps = (XPropertySet) UnoRuntime.queryInterface(
                    XPropertySet.class, xImage);
            
            // helper-stuff to let OOo create an internal name of the graphic
            // that can be used later (internal name consists of various checksums)
            xBitmapContainer.insertByName(imgID, imgURL); 

            Object obj = xBitmapContainer.getByName(imgID);
            internalURL = AnyConverter.toString(obj);
            
            
            // Setting the anchor type
            xProps.setPropertyValue("AnchorType",
                       com.sun.star.text.TextContentAnchorType.AT_PARAGRAPH );  //AT_PARAGRAPH じゃないとうまく行かない
            // Setting the graphic url
            xProps.setPropertyValue( "GraphicURL", internalURL );
            xProps.setPropertyValue("TextWrap", WrapTextMode.NONE ); 
            xProps.setPropertyValue("HoriOrient", HoriOrientation.LEFT); 
            xProps.setPropertyValue("VertOrient", VertOrientation.NONE); 

            //xProps.setPropertyValue("Width", (int) 4000); // original: 88 px
            //xProps.setPropertyValue("Height", (int) 1550); // original: 31 px
            
            
            XGraphicProvider xGraphicProvider = (XGraphicProvider) 
                    UnoRuntime.queryInterface(XGraphicProvider.class, 
                    xMCF.createInstanceWithContext("com.sun.star.graphic.GraphicProvider", xContext)); 
            PropertyValue prop[] = new PropertyValue[1];
            prop[0] = new PropertyValue();
            prop[0].Name = "URL";
            prop[0].Value = internalURL;
            XGraphic xNewGraphic = xGraphicProvider.queryGraphic(prop);
            XBitmap xBitmap = (XBitmap) UnoRuntime.queryInterface(
                    XBitmap.class,     xNewGraphic);
            Size sz = xBitmap.getSize();
            System.out.println(sz.Width + "/" + sz.Height);
            xProps.setPropertyValue("Width", (int) ((sz.Width / 96.0) * 2540.0)); // original: 88 px
            xProps.setPropertyValue("Height", (int) ((sz.Height / 96.0) * 2540.0)); // original: 31 px
            
            // inser the graphic at the cursor position
            xText.insertTextContent(xCursor, xImage, false);
            
            // remove the helper-entry
            xBitmapContainer.removeByName(imgID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Ensure that there are no disabled commands in the user layer. The
     * implementation removes all commands from the disabled set!
     */
    private static void enableCommands(XMultiServiceFactory xConfigProvider) {
        // Set the root path for our configuration access
        com.sun.star.beans.PropertyValue[] lParams =
            new com.sun.star.beans.PropertyValue[1];

        lParams[0] = new com.sun.star.beans.PropertyValue();
        lParams[0].Name  = new String("nodepath");
        lParams[0].Value = "/org.openoffice.Office.Commands/Execute/Disabled";

        try {
            // Create configuration update access to have write access to the
            // configuration
            Object xAccess = xConfigProvider.createInstanceWithArguments(
                             "com.sun.star.configuration.ConfigurationUpdateAccess",
                             lParams );

            com.sun.star.container.XNameAccess xNameAccess =
                (com.sun.star.container.XNameAccess)UnoRuntime.queryInterface(
                    com.sun.star.container.XNameAccess.class, xAccess );

            if ( xNameAccess != null ) {
                // We need the XNameContainer interface to remove the nodes by name
                com.sun.star.container.XNameContainer xNameContainer =
                    (com.sun.star.container.XNameContainer)
                    UnoRuntime.queryInterface(
                        com.sun.star.container.XNameContainer.class, xAccess );

                // Retrieves the names of all Disabled nodes
                String[] aCommandsSeq = xNameAccess.getElementNames();
                for ( int n = 0; n < aCommandsSeq.length; n++ ) {
                    try {
                        // remove the node
                        xNameContainer.removeByName( aCommandsSeq[n] );
                    }
                    catch ( com.sun.star.lang.WrappedTargetException e ) {
                    }
                    catch ( com.sun.star.container.NoSuchElementException e ) {
                    }
                }
            }

            // Commit our changes
            com.sun.star.util.XChangesBatch xFlush =
                (com.sun.star.util.XChangesBatch)UnoRuntime.queryInterface(
                    com.sun.star.util.XChangesBatch.class, xAccess);

            xFlush.commitChanges();
        }
        catch ( com.sun.star.uno.Exception e ) {
            System.out.println( "Exception detected!" );
            System.out.println( e );
        }
    }
    
    public  com.sun.star.frame.XDesktop getDesktop() throws Exception {
        com.sun.star.frame.XDesktop xDesktop = null;
        com.sun.star.lang.XMultiComponentFactory xMCF = null;

        try {
            makeContext = null;

            //Add By HDM
            //String oooExeFolder = "/usr/bin/libreoffice4.1";
            String oooExeFolder = LibreExePath;
            makeContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
            
            // get the remote office component context
            //makeContext = com.sun.star.comp.helper.Bootstrap.bootstrap();

            // get the remote office service manager
            xMCF = makeContext.getServiceManager();
            if( xMCF != null ) {
                System.out.println("Connected to a running office ...");

                Object oDesktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", makeContext);
                xDesktop = (com.sun.star.frame.XDesktop) UnoRuntime.queryInterface(
                    com.sun.star.frame.XDesktop.class, oDesktop);
            } else {
                System.out.println( "Can't create a desktop. No connection, no remote office servicemanager available!" );
            }
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
            //Msg
            JOptionPane.showMessageDialog(null, "LibreOfficeの実行ファイルのあるフォルダが\n正しく設定されているか確認してください。\n処理を中断します。");
            throw e;
        }
        return xDesktop;
    }
    
    
    public  com.sun.star.text.XTextDocument readTextdocument(
        com.sun.star.frame.XDesktop xDesktop , String fname)
    {
        com.sun.star.text.XTextDocument aTextDocument = null;

        try {
            com.sun.star.lang.XComponent xComponent = readDocument(xDesktop,
                                                                        "swriter", fname);
            aTextDocument = (com.sun.star.text.XTextDocument)
                UnoRuntime.queryInterface(
                    com.sun.star.text.XTextDocument.class, xComponent);
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
        }

        return aTextDocument;
    }
    
    protected  com.sun.star.lang.XComponent readDocument(
        com.sun.star.frame.XDesktop xDesktop,
        String sDocumentType , String fname)
    {
        String sURL = "private:factory/" + sDocumentType;

        XComponent xComponent = null;
        com.sun.star.frame.XComponentLoader xComponentLoader = null;

        try {
            xComponentLoader = (com.sun.star.frame.XComponentLoader)
                UnoRuntime.queryInterface(
                    com.sun.star.frame.XComponentLoader.class, xDesktop);
            // load template with User fields and bookmark
            java.io.File sourceFile = new java.io.File(fname);
            StringBuilder sTemplateFileUrl = new StringBuilder("file:///");
            sTemplateFileUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
            //xComponent =
            //    newDocComponentFromTemplate( sTemplateFileUrl.toString() );
            
            PropertyValue[] loadProps = new PropertyValue[1];
            loadProps[0] = new PropertyValue();
            loadProps[0].Name = "AsTemplate";
            loadProps[0].Value = true;
            // load
            xComponent = xComponentLoader.loadComponentFromURL(sTemplateFileUrl.toString(), "_blank",
                                                         0, loadProps);
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
        }

        return xComponent ;
    }
}
