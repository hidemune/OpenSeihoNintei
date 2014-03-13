package openseihonintei;


import com.sun.star.beans.PropertyValue;
import com.sun.star.container.XIndexAccess;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XDispatchHelper;
import com.sun.star.frame.XDispatchProvider;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheets;
import com.sun.star.table.XCell;
import com.sun.star.table.XCellRange;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
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
 *  C:\Program Files (x86)\OpenOffice 4\program
 * @author hdm
 */
public class LibreCalc {
public static String LibreExePath = "C:\\Program Files (x86)\\OpenOffice 4\\program";
com.sun.star.uno.XComponentContext makeContext = null;
com.sun.star.frame.XDesktop makeDesktop = null;
XSpreadsheetDocument xSheetDocumentOut = null;
private static XDispatchHelper xDispatchHelperOut = null;
private static XDispatchProvider xDocDispatchProviderOut = null;

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
            
            xSheetDocumentOut = openCreatedocument(makeDesktop);
            initSheet();
            
            append(null);
            
        }
        catch( Exception e) {
            e.printStackTrace(System.err);
            return;
        }
        JOptionPane.showMessageDialog(null, "処理が完了しました。\n目次を右クリックして「目次と索引の更新」を実行してください。\nその後、名前をつけて保存してください。");
        System.out.println("Done");
    }
    
    
    public XSpreadsheetDocument openCreatedocument(XDesktop xDesktop)
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
                
                java.io.File sourceFile = new java.io.File("print/TemplateA4P.ods");
                StringBuilder sLoadUrl = new StringBuilder("file:///");
                sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                Object oDocToStore = xCompLoader.loadComponentFromURL(
                    sLoadUrl.toString(), "_blank", 0, propertyValue );
                aSheetDocument = (XSpreadsheetDocument) UnoRuntime.queryInterface( XSpreadsheetDocument.class, oDocToStore);
                
            }catch (Exception e) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, e);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\n「print/TemplateA4P.ods」がカレントディレクトリにあるか確認してください。");
            }
        
        return aSheetDocument;
    }

    public void append(ArrayList<String> ArrStr[]) throws IllegalArgumentException, Exception {
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
                
                for (int i = 0; i < 3; i++) {

                    //テンプレート読み込み
                    com.sun.star.beans.PropertyValue[] propertyValue =
                        new com.sun.star.beans.PropertyValue[1];
                    propertyValue[0] = new com.sun.star.beans.PropertyValue();
                    propertyValue[0].Name = "Hidden";           //Hidden AsTemplate
                    propertyValue[0].Value = true;
                    java.io.File sourceFile = new java.io.File("print/chosyo2.ods");
                    StringBuilder sLoadUrl = new StringBuilder("file:///");
                    sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                    Object oDocToStore = xCompLoader.loadComponentFromURL(
                        sLoadUrl.toString(), "_blank", 0, propertyValue );

                    //com.sun.star.text.XTextDocument aSheetDocument = (com.sun.star.text.XTextDocument)
                    //    UnoRuntime.queryInterface(
                    //        com.sun.star.text.XTextDocument.class, oDocToStore);
                    
                    
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
                    //XSpreadsheetDocument myDoc = (XSpreadsheetDocument)UnoRuntime.queryInterface( XSpreadsheetDocument.class, oDocToStore);
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
                    xCellRange = xSheet.getCellRangeByName("CaseNo");
                    if (i == 0) {
                        xCellRange.getCellByPosition(0, 0).setFormula("ZZZ-0000");
                    } else if (i == 1){
                        xCellRange.getCellByPosition(0, 0).setFormula("ZZZ-1111");
                    } else {
                        xCellRange.getCellByPosition(0, 0).setFormula("ZZZ-2222");
                    }



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
                    a[0].Value = "A1:I52";
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
                    a[0].Value = "A" + (i * 52 + 1);
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
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\nカレントディレクトリに「print/chosyo2.ods」があるか確認してください。");
                throw ex;
                //return;
            } catch (Exception ex) {
                Logger.getLogger(LibreCalc.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "エラーが発生しました。");
                throw ex;
                //return;
            }
    }
    public void initSheet() throws IllegalArgumentException, Exception {
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
                java.io.File sourceFile = new java.io.File("print/chosyo2.ods");
                StringBuilder sLoadUrl = new StringBuilder("file:///");
                sLoadUrl.append(sourceFile.getCanonicalPath().replace('\\', '/'));
                Object oDocToStore = xCompLoader.loadComponentFromURL(
                    sLoadUrl.toString(), "_blank", 0, propertyValue );

                //com.sun.star.text.XTextDocument aSheetDocument = (com.sun.star.text.XTextDocument)
                //    UnoRuntime.queryInterface(
                //        com.sun.star.text.XTextDocument.class, oDocToStore);


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
                JOptionPane.showMessageDialog(null, "エラーが発生しました。\nカレントディレクトリに「print/chosyo2.ods」があるか確認してください。");
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
    
}