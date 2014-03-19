package openseihonintei;


import com.sun.star.awt.Point;
import com.sun.star.awt.Size;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.XPropertySetInfo;
import com.sun.star.container.ElementExistException;
import com.sun.star.container.XIndexAccess;
import com.sun.star.container.XNameContainer;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPageSupplier;
import com.sun.star.drawing.XShape;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.uno.AnyConverter;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import ooo.connector.BootstrapSocketConnector;
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  C:\Program Files (x86)\OpenOffice 4\program
 * @author hdm
 */
public class LibreCalc {
public static String LibreExePath = "C:\\Program Files (x86)\\OpenOffice 4\\program";
//public static String LibreExePath = "C:\\Program Files (x86)\\LibreOffice 4\\program";
com.sun.star.uno.XComponentContext makeContext = null;
com.sun.star.frame.XDesktop makeDesktop = null;
XSpreadsheetDocument xSheetDocumentOut = null;
private static XDispatchHelper xDispatchHelperOut = null;
private static XDispatchProvider xDocDispatchProviderOut = null;

    /**
     * 帳票をCalcで出力します。
     * @param ArrStr 
     * @param printName 
     *  Template .ods File name
     * @param paperType 
     *  A4P : A4 - Portrait
     *  A4L : A4 - Landscape
     * @param cellRangeEnd
     *  ex. "I52"
     */
    public void makeCalcFile(ArrayList<String[][]> ArrStr, String printName, String paperType, String cellRangeEnd) {
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
            
            xSheetDocumentOut = openCreatedocument(makeDesktop, paperType);
            initSheet(printName);
            
            append(ArrStr, printName, paperType, cellRangeEnd);
            
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
            return;
        }
        JOptionPane.showMessageDialog(null, "処理が完了しました。\n目次を右クリックして「目次と索引の更新」を実行してください。\nその後、名前をつけて保存してください。");
        System.out.println("Done");
    }
    
    
    public XSpreadsheetDocument openCreatedocument(XDesktop xDesktop, String paperType)
    {
        XSpreadsheetDocument aSheetDocument = null;
        
        
            try {
                // get the remote office component context
                String oooExeFolder = LibreExePath;
                makeContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                //makeContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
                System.out.println("Connected to a office ...");

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
                    new com.sun.star.beans.PropertyValue[2];
                propertyValue[0] = new com.sun.star.beans.PropertyValue();
                propertyValue[0].Name = "AsTemplate";
                propertyValue[0].Value = true;
                
                propertyValue[1] = new com.sun.star.beans.PropertyValue();
                propertyValue[1].Name = "Hidden";
                propertyValue[1].Value = false;
                
                java.io.File sourceFile = new java.io.File("print/Template" + paperType + ".ods");
                StringBuilder sLoadUrl = new StringBuilder("file:///");
                sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                Object oDocToStore = xCompLoader.loadComponentFromURL(
                    sLoadUrl.toString(), "_blank", 0, propertyValue );
                aSheetDocument = (XSpreadsheetDocument) UnoRuntime.queryInterface( XSpreadsheetDocument.class, oDocToStore);
                
            }catch (Exception e) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\n「print/Template" + paperType + ".ods」がカレントディレクトリにあるか確認してください。");
            }
        
        return aSheetDocument;
    }

    public void append(ArrayList<String[][]> ArrStr, String printName, String paper, String cellRangeEnd) throws IllegalArgumentException, Exception {
        XComponentContext xContext = null;
        XModel xDocModel = null;
        
            try {
                //テンプレートファイル
                // get the remote office component context
                String oooExeFolder = LibreExePath;
                xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                //xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
                System.out.println("Connected to a office ...");

                // get the remote office service manager
                com.sun.star.lang.XMultiComponentFactory xMCF =
                    xContext.getServiceManager();
                Object oDesktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", xContext);
                
                com.sun.star.frame.XComponentLoader xCompLoader =
                    (com.sun.star.frame.XComponentLoader)
                         UnoRuntime.queryInterface(
                             com.sun.star.frame.XComponentLoader.class, oDesktop);
                
                
                //コマンドを発行可能にする
                XMultiComponentFactory xRemoteServiceManager = null;
                xRemoteServiceManager = makeContext.getServiceManager();
                Object configProvider = xRemoteServiceManager.createInstanceWithContext(
                              "com.sun.star.configuration.ConfigurationProvider",
                              makeContext );
                XMultiServiceFactory xConfigProvider = null;
                xConfigProvider = (com.sun.star.lang.XMultiServiceFactory)
                    UnoRuntime.queryInterface(
                        com.sun.star.lang.XMultiServiceFactory.class, configProvider );
                enableCommands(xConfigProvider);
                
                //セル範囲文字列から、列数を取得
                Pattern p = Pattern.compile("[A-Z]*");
                Matcher m = p.matcher(cellRangeEnd);
                String result = m.replaceAll("");
                int RowNum = Integer.parseInt(result);
                System.err.println("RowNum:" + RowNum);
                for (int i = 0; i < ArrStr.size(); i++) {

                    //テンプレート読み込み
                    com.sun.star.beans.PropertyValue[] propertyValue =
                        new com.sun.star.beans.PropertyValue[2];
                    propertyValue[0] = new com.sun.star.beans.PropertyValue();
                    propertyValue[0].Name = "AsTemplate";
                    propertyValue[0].Value = true;
                    propertyValue[1] = new com.sun.star.beans.PropertyValue();
                    propertyValue[1].Name = "Hidden";
                    propertyValue[1].Value = false;
                    java.io.File sourceFile = new java.io.File("print/" + printName);
                    StringBuilder sLoadUrl = new StringBuilder("file:///");
                    sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                    Object oDocToStore = xCompLoader.loadComponentFromURL(
                        sLoadUrl.toString(), "_blank", 0, propertyValue );
                    
                    com.sun.star.frame.XStorable xStorable =
                        (com.sun.star.frame.XStorable)UnoRuntime.queryInterface(
                            com.sun.star.frame.XStorable.class, oDocToStore );
                    sourceFile = new java.io.File("tmp" + i+ ".ods");
                    StringBuilder sSaveUrl = new StringBuilder("file:///");
                    sSaveUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                    // save
                    propertyValue = new com.sun.star.beans.PropertyValue[ 2 ];
                    propertyValue[0] = new com.sun.star.beans.PropertyValue();
                    propertyValue[0].Name = "Overwrite";
                    propertyValue[0].Value = new Boolean(true);
                    propertyValue[1] = new com.sun.star.beans.PropertyValue();
                    propertyValue[1].Name = "FilterName";
                    propertyValue[1].Value = "calc8";
                    xStorable.storeAsURL( sSaveUrl.toString(), propertyValue );
                    
                    //Calc
                    XSpreadsheetDocument myDoc = (XSpreadsheetDocument)UnoRuntime.queryInterface( XSpreadsheetDocument.class, xStorable);
                    
                    XSpreadsheets xSheets = myDoc.getSheets() ;
                    XSpreadsheet xSheet = null;
                    XCellRange xCellRange = null;
                    XCell xCell = null;

                    //値をセット
                    XIndexAccess oIndexSheets = (XIndexAccess) UnoRuntime.queryInterface(
                        XIndexAccess.class, xSheets);
                    xSheet = (XSpreadsheet) UnoRuntime.queryInterface(
                        XSpreadsheet.class, oIndexSheets.getByIndex(0));
                    for (int j = 0; j < ArrStr.get(i).length; j++) {
                        String name = ArrStr.get(i)[j][0];
                        String value = ArrStr.get(i)[j][1];
                        try {
                            xCellRange = xSheet.getCellRangeByName(name);
                            xCellRange.getCellByPosition(0, 0).setFormula(value);
                        } catch (Exception e) {
                            System.err.println("Err : " + name + "/" + value);
                        }
                    }
                    
                    // Creating a string for the graphic url
                    java.io.File sourceFileImg = new java.io.File("print/inei.png");
                    StringBuffer sUrl = new StringBuffer("file:///");
                    sUrl.append(sourceFileImg.getCanonicalPath().replace('\\', '/'));
                    System.out.println( "insert graphic \"" + sUrl + "\"");
                    //画像読み込み
                    putImage(xContext, myDoc, xSheet, sUrl.toString(), new Point(1000, 2000), new Size(3000, 3000));
                    
                    //copy
                    Object dispatchHelper = xMCF.createInstanceWithContext("com.sun.star.frame.DispatchHelper", makeContext);
                    XDispatchHelper xDispatchHelper =
                      (XDispatchHelper)UnoRuntime.queryInterface(XDispatchHelper.class, dispatchHelper); 
                    
                    xDocModel = (XModel) UnoRuntime.queryInterface(XModel.class, myDoc);
                    XController xDocController = (XController) UnoRuntime.queryInterface(
                            XController.class, xDocModel.getCurrentController());
                    XDispatchProvider xDocDispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface(
                            XDispatchProvider.class, xDocController);
                    //////
                    com.sun.star.beans.PropertyValue[] a =
                        new com.sun.star.beans.PropertyValue[1];
                    a[0] = new com.sun.star.beans.PropertyValue();
                    a[0].Name = "ToPoint";
                    a[0].Value = "A1:" + cellRangeEnd;;
                    a[0].Handle = 0;
                    a[0].State = com.sun.star.beans.PropertyState.DIRECT_VALUE;
                    xDispatchHelper.executeDispatch(xDocDispatchProvider, ".uno:GoToCell", "", 0, a);
                    xDispatchHelper.executeDispatch(xDocDispatchProvider, ".uno:Copy", "", 0, new PropertyValue[0]);
                    
                    //貼り付け
                    Object dispatchHelperOut = xMCF.createInstanceWithContext("com.sun.star.frame.DispatchHelper", makeContext);
                    xDispatchHelperOut =
                      (XDispatchHelper)UnoRuntime.queryInterface(XDispatchHelper.class, dispatchHelperOut); 
                    
                    XModel xDocModelOut = (XModel) UnoRuntime.queryInterface(XModel.class, xSheetDocumentOut);
                    XController  xDocControllerOut = (XController) UnoRuntime.queryInterface(
                            XController.class, xDocModelOut.getCurrentController());
                    xDocDispatchProviderOut = (XDispatchProvider) UnoRuntime.queryInterface(
                            XDispatchProvider.class, xDocControllerOut);
                    
                    
                    //PasteThread thread = new PasteThread();
                    //thread.start();
                    a[0] = new com.sun.star.beans.PropertyValue();
                    a[0].Name = "ToPoint";
                    a[0].Value = "A" + (i * RowNum + 1);
                    a[0].Handle = 0;
                    a[0].State = com.sun.star.beans.PropertyState.DIRECT_VALUE;
                    xDispatchHelperOut.executeDispatch(xDocDispatchProviderOut, ".uno:GoToCell", "", 0, a);
                    xDispatchHelperOut.executeDispatch(xDocDispatchProviderOut, ".uno:Paste", "", 0, new PropertyValue[0]);
                    
                    //xText.setString("Page " + i);
                    
                    //閉じる
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
                }
            } catch (com.sun.star.lang.IllegalArgumentException ex) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\nカレントディレクトリに「print/" + printName + "」があるか確認してください。");
                throw ex;
                //return; ここには来ない
            } catch (Exception ex) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。");
                throw ex;
                //return; ここには来ない
            }
            
        //tmpファイルの削除
        for (int i = 0; i < ArrStr.size(); i++) {
            File file = new File("tmp" + i + ".odt");
            if (file.exists()){
                file.delete();
            }
        }
    }
    public void initSheet(String printName) throws IllegalArgumentException, Exception {
        XComponentContext xContext = null;
        XModel xDocModel = null;
        
            try {
                //テンプレートファイル
                // get the remote office component context
                String oooExeFolder = LibreExePath;
                xContext = BootstrapSocketConnector.bootstrap(oooExeFolder);
                //xContext = com.sun.star.comp.helper.Bootstrap.bootstrap();
                System.out.println("Connected to a office ...");

                // get the remote office service manager
                com.sun.star.lang.XMultiComponentFactory xMCF =
                    xContext.getServiceManager();
                Object oDesktop = xMCF.createInstanceWithContext(
                    "com.sun.star.frame.Desktop", xContext);
                
                com.sun.star.frame.XComponentLoader xCompLoader =
                    (com.sun.star.frame.XComponentLoader)
                         UnoRuntime.queryInterface(
                             com.sun.star.frame.XComponentLoader.class, oDesktop);
                
                
                //コマンドを発行可能にする
                XMultiComponentFactory xRemoteServiceManager = null;
                xRemoteServiceManager = makeContext.getServiceManager();
                Object configProvider = xRemoteServiceManager.createInstanceWithContext(
                              "com.sun.star.configuration.ConfigurationProvider",
                              makeContext );
                XMultiServiceFactory xConfigProvider = null;
                xConfigProvider = (com.sun.star.lang.XMultiServiceFactory)
                    UnoRuntime.queryInterface(
                        com.sun.star.lang.XMultiServiceFactory.class, configProvider );
                enableCommands(xConfigProvider);
                

                //テンプレート読み込み
                com.sun.star.beans.PropertyValue[] propertyValue =
                    new com.sun.star.beans.PropertyValue[1];
                propertyValue[0] = new com.sun.star.beans.PropertyValue();
                propertyValue[0].Name = "Hidden";           //Hidden AsTemplate
                propertyValue[0].Value = true;
                java.io.File sourceFile = new java.io.File("print/" + printName);
                StringBuilder sLoadUrl = new StringBuilder("file:///");
                sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                Object oDocToStore = xCompLoader.loadComponentFromURL(
                    sLoadUrl.toString(), "_blank", 0, propertyValue );

                com.sun.star.frame.XStorable xStorable =
                    (com.sun.star.frame.XStorable)UnoRuntime.queryInterface(
                        com.sun.star.frame.XStorable.class, oDocToStore );
                sourceFile = new java.io.File("tmp.ods");
                StringBuilder sSaveUrl = new StringBuilder("file:///");
                sSaveUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                // save
                propertyValue = new com.sun.star.beans.PropertyValue[ 2 ];
                propertyValue[0] = new com.sun.star.beans.PropertyValue();
                propertyValue[0].Name = "Overwrite";
                propertyValue[0].Value = new Boolean(true);
                propertyValue[1] = new com.sun.star.beans.PropertyValue();
                propertyValue[1].Name = "FilterName";
                propertyValue[1].Value = "calc8";
                xStorable.storeAsURL( sSaveUrl.toString(), propertyValue );

                //Calc
                XSpreadsheetDocument myDoc = (XSpreadsheetDocument)UnoRuntime.queryInterface( XSpreadsheetDocument.class, xStorable);
                
                //copy
                Object dispatchHelper = xMCF.createInstanceWithContext("com.sun.star.frame.DispatchHelper", makeContext);
                XDispatchHelper xDispatchHelper =
                  (XDispatchHelper)UnoRuntime.queryInterface(XDispatchHelper.class, dispatchHelper); 

                xDocModel = (XModel) UnoRuntime.queryInterface(XModel.class, myDoc);
                XController xDocController = (XController) UnoRuntime.queryInterface(
                        XController.class, xDocModel.getCurrentController());
                XDispatchProvider xDocDispatchProvider = (XDispatchProvider) UnoRuntime.queryInterface(
                        XDispatchProvider.class, xDocController);
                
                xDispatchHelper.executeDispatch(xDocDispatchProvider, ".uno:SelectAll", "", 0, new PropertyValue[0]);
                xDispatchHelper.executeDispatch(xDocDispatchProvider, ".uno:Copy", "", 0, new PropertyValue[0]);

                //貼り付け
                Object dispatchHelperOut = xMCF.createInstanceWithContext("com.sun.star.frame.DispatchHelper", makeContext);
                xDispatchHelperOut =
                  (XDispatchHelper)UnoRuntime.queryInterface(XDispatchHelper.class, dispatchHelperOut); 

                XModel xDocModelOut = (XModel) UnoRuntime.queryInterface(XModel.class, xSheetDocumentOut);
                XController  xDocControllerOut = (XController) UnoRuntime.queryInterface(
                        XController.class, xDocModelOut.getCurrentController());
                xDocDispatchProviderOut = (XDispatchProvider) UnoRuntime.queryInterface(
                        XDispatchProvider.class, xDocControllerOut);
                
                xDispatchHelperOut.executeDispatch(xDocDispatchProviderOut, ".uno:SelectAll", "", 0, new PropertyValue[0]);
                xDispatchHelperOut.executeDispatch(xDocDispatchProviderOut, ".uno:Paste", "", 0, new PropertyValue[0]);

                //閉じる
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
            } catch (com.sun.star.lang.IllegalArgumentException ex) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\nカレントディレクトリに「print/" + printName + "」があるか確認してください。");
                throw ex;
            } catch (Exception ex) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。");
                throw ex;
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
    
// this one loads an image (.jpg) int the specified calc file at a specified location w/ the specified size ...
    private static void putImage(XComponentContext xContext, XSpreadsheetDocument myDoc, XSpreadsheet xSpreadsheet, String sFilePath, Point imgPos,Size imgSize) {

        XDrawPage xDrawPage = null; // return this to be able to delete imm after printing
        String imgID = "tempImageId" + System.currentTimeMillis();
        
        try {

            XSpreadsheetDocument xSpreadsheetDocument = (XSpreadsheetDocument) UnoRuntime.queryInterface(XSpreadsheetDocument.class,
                    xContext);

            XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class, myDoc);
            XController xController = xModel.getCurrentController();

            XSpreadsheetView xSpreadsheetView = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class, xController);
            xSpreadsheetView.setActiveSheet((XSpreadsheet) xSpreadsheet);


            XFrame xFrame = xController.getFrame();  // gets Frame of Spreadsheet loaded above !
            XSpreadsheetDocument xDoc = (XSpreadsheetDocument)UnoRuntime.queryInterface(
                                                XSpreadsheetDocument.class, xModel);

            XSpreadsheetView xsv = (XSpreadsheetView)UnoRuntime.queryInterface(
                                                XSpreadsheetView.class, xFrame.getController());
            // TODO: replace w/ setting this to either sheetName or the sheetObj
            XSpreadsheet curSheet = xsv.getActiveSheet();


            XMultiComponentFactory mxMCF = xContext.getServiceManager();
            XMultiServiceFactory x_msf = (XMultiServiceFactory) UnoRuntime.queryInterface(
            XMultiServiceFactory.class, xModel);

            // a bitmap container is used to hold graphic(s) permanently (as opposed to just having a link to the graphic(s)
            // in the document ...
            XNameContainer xBitmapContainer = null;
            try {
                xBitmapContainer = (XNameContainer) UnoRuntime.queryInterface(
                            XNameContainer.class, x_msf.createInstance("com.sun.star.drawing.BitmapTable"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "BitmapContainer not found in " + "" + "!");
                ex.printStackTrace();
            }

/*
            String sFilePath = "file:///" + sGraphFile.replaceAll("[/|\\\\]+", "/");
            String internalName = "";
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(sFilePath.getBytes(), 0, sFilePath.length());
                internalName = new BigInteger(1, md.digest()).toString(16);
            } catch (NoSuchAlgorithmException ex) {
                ex.printStackTrace();
            }
*/
            Object imgTemp = x_msf.createInstance("com.sun.star.drawing.GraphicObjectShape");
            XShape xImage = (XShape)UnoRuntime.queryInterface(XShape.class, imgTemp);

            XPropertySet xImgPropSet = ( XPropertySet )UnoRuntime.queryInterface(XPropertySet.class, xImage );
            XPropertySetInfo xIPSInfo = xImgPropSet.getPropertySetInfo();
            xImgPropSet.setPropertyValue("AnchorType",
                        com.sun.star.text.TextContentAnchorType.AS_CHARACTER);

            //sFilePath = "file:///" + sGraphFile.replaceAll("[/|\\\\]+", "/");
        
          try {
             xBitmapContainer.insertByName(imgID, sFilePath);
          } catch (ElementExistException ex) {
             JOptionPane.showMessageDialog(null, "Element exists !, can't add the new image using this ID");
             ex.printStackTrace();
          }
            String internalURL = AnyConverter.toString(xBitmapContainer.getByName(imgID));
            xImgPropSet.setPropertyValue("GraphicURL", internalURL);
            xImage.setPosition(imgPos);  // Values as used/shown by OpenOffice Calc, right click on img 'Position & Grösse'
            xImage.setSize(imgSize);    // Values as used/shown by OpenOffice Calc, right click on img 'Position & Grösse'

            // 2010-Apr-14: wbs/
            // BugFix: [ ]  open
            //
            // added in an attempt to make the imgs discernable to OO, since saving the sheet and reopening it
            // results currently in having all imgs placed earlier being all the same (actually the same as
            // the last one placed.
            xImgPropSet.setPropertyValue("Title",sFilePath);
            xImgPropSet.setPropertyValue("Name", imgID);

            com.sun.star.beans.Property[] aProps = xIPSInfo.getProperties();
            for (int n=0;n<aProps.length;n++){
                System.out.println(aProps[n].Name + ": Value:"+ xImgPropSet.getPropertyValue(aProps[n].Name));
            }
            System.out.println("eo PropSet for XShape (GraphObj)");

            // and finally we add it to the DrawPage of this sheet
            XDrawPageSupplier oDPS = (XDrawPageSupplier) UnoRuntime.queryInterface(
                XDrawPageSupplier.class, curSheet);
            xDrawPage = oDPS.getDrawPage();
            xDrawPage.add(xImage);

            System.out.println("Img added.");

            // remove the helper-entry
            xBitmapContainer.removeByName(imgID);
        }   catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
