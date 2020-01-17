//package win.doyto.service.generator.module.table;
//
//import org.junit.Test;
//import org.springframework.test.annotation.Commit;
//import win.doyto.service.generator.module.ImportController;
//
//import javax.annotation.Resource;
//
//public class ImportControllerTest  {
//
//    @Resource
//    private ImportController importController;
//
//    @Commit
//    public void importAll() {
//        importController.importDatabase(1);
//    }
//
//    public void importModule() {
//        String createSql = "CREATE TABLE test_category\n" +
//                "(\n" +
//                "    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,\n" +
//                "    name VARCHAR(45) NOT NULL,\n" +
//                "    memo VARCHAR(225),\n" +
//                "    zoneId INT(11) NOT NULL,\n" +
//                "    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL\n" +
//                ")";
//        importController.importModule(1, createSql);
//    }
//
//}