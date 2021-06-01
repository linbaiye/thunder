package org.fastj.thunder;

import com.intellij.psi.*;
import com.intellij.psi.search.ProjectScope;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import org.fastj.thunder.modifier.repository.RepositoryCodeModifier;
import org.intellij.lang.annotations.Language;

import java.util.Optional;

public class RepositoryCodeModifierTest extends LightJavaCodeInsightFixtureTestCase {

    @Language("JAVA")
    private final static String ENTITY_CLASS = "package org.fastj.thunder.model;" +
            "import java.math.BigDecimal;\n" +
            "import java.util.Date;\n"+
            "public class Entity implements Serializable {" +
            "    private String firstName;\n" +
            "    private String lastName;\n" +
            "    private String status;\n" +
            "    private Date date;\n" +
            "}";

    @Language("JAVA")
    private final static String DAO_CLASS = "package org.fastj.thunder.dao;" +
            "import com.baomidou.mybatisplus.core.mapper.BaseMapper;\n" +
            "import org.fastj.thunder.model.Entity;\n" +
            "public interface EntityDao extends BaseMapper<Entity> {\n" +
            "}";

    @Language("JAVA")
    private final static String BASE_MAPPER = "package com.baomidou.mybatisplus.core.mapper;" +
            "public interface BaseMapper<T> {\n" +
            "T selectOne(LambdaQueryWrapper<T> queryWrapper);\n" +
            "}";


    @Override
    public void setUp() throws Exception {
        super.setUp();
        myFixture.addFileToProject("main/java/org/fastj/thunder/model/Entity.java", ENTITY_CLASS);
        myFixture.addFileToProject("main/java/org/fastj/thunder/dao/EntityDao.java", DAO_CLASS);
        myFixture.addFileToProject("main/java/com/baomidou/mybatisplus/core/mapper/BaseMapper.java", BASE_MAPPER);
    }

    public void testQuery() {
        @Language("JAVA")
        String current = "package org.fastj.thunder.repository;\n" +
                "import org.fastj.thunder.model.Entity;\n" +
                "import org.fastj.thunder.dao.EntityDao;\n" +
                "public class EntityRepository {\n" +
                "   private EntityDao entityDao;\n" +
                "   public Entity findByLastNameAndFirstNameAndStatusInAndDate(String last, String first, List<String> status, Date date) {\n" +
                "       this.entityDao" +
                "   }" +
                "   public List<Entity> findByLastNameAndStatusIn(String lastName, List<Status> statusList) {\n" +
                "       this.entityDao" +
                "   }" +
                "}\n";
        myFixture.addFileToProject("main/java/org/fastj/thunder/repository/EntityRepository.java", current);
        PsiClass modifiedClass = JavaPsiFacade.getInstance(getProject()).findClass("org.fastj.thunder.repository.EntityRepository",
                ProjectScope.getProjectScope(getProject()));
        PsiMethod method = modifiedClass.findMethodsByName("findByLastNameAndFirstNameAndStatusInAndDate", false)[0];
        PsiField dao = modifiedClass.findFieldByName("entityDao", false);
        PsiClass entity = JavaPsiFacade.getInstance(getProject()).findClass("org.fastj.thunder.model.Entity",
                ProjectScope.getProjectScope(getProject()));
        Optional<RepositoryCodeModifier> optional = RepositoryCodeModifier.from(method, entity, method.getBody().getChildren()[2], dao);
        optional.ifPresent(e -> {
            e.tryModify();
            System.out.println(modifiedClass.getText());
        });
    }
}
