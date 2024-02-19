//package dev.quarkusclub.bank;
//
//import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
//
//import java.util.Collections;
//import java.util.Map;
//
//public class PostgresResource implements
//        QuarkusTestResourceLifecycleManager {
//
//    static PostgreSQLContainer<?> db =
//            new PostgreSQLContainer<>("postgres:13")
//                    .withDatabaseName("tododb")
//                    .withUsername("todouser")
//                    .withPassword("todopw");
//
//    @Override
//    public Map<String, String> start() {
//        db.start();
//        return Collections.singletonMap(
//                "quarkus.datasource.url", db.getJdbcUrl()
//        );
//    }
//
//    @Override
//    public void stop() {
//        db.stop();
//    }
//}