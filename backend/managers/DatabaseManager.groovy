package managers

import entities.Employment
import entities.NewEnterprise

import java.sql.Connection
import java.sql.SQLException

import entities.NewCandidate
import db.Queries

class DatabaseManager {

    Connection connection

    DatabaseManager(Connection connection){
        this.connection = connection
    }

    //CRUD Candidates
    boolean saveNewCandidate(NewCandidate candidate) {
        if (!candidate.isAllSet()) {
            return false
        }

        // Desativa o auto-commit para controle manual
        boolean originalAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
            // Executa a query completa (já inclui BEGIN e COMMIT)
            connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.insertUsersTable(candidate))
                if(!this.getPostalCodeId(candidate)){
                    statement.execute(Queries.insertPostalCodesTable(candidate))
                }
                statement.execute(Queries.insertCandidatesTable(candidate))
                statement.execute(Queries.insertCandidateSkillTable(candidate))
            }

            connection.commit() // Confirma transação
            return true

        } catch (SQLException e) {
            connection.rollback() // Reverte em caso de erro
            e.printStackTrace()
            return false
        } finally {
            connection.autoCommit = originalAutoCommit // Restaura o auto-commit original
        }
    }

    NewCandidate getCandidateById(int id) {
        try {
            // Usa withCloseable para fechar automaticamente Statement e ResultSet
            return this.connection.createStatement().withCloseable { statement ->
                statement.executeQuery(Queries.selectCandidateById(id)).withCloseable { resultSet ->
                    if (resultSet.next()) {
                        // Mapeia os dados (com tratamento de valores nulos)
                        Map params = [
                                id: resultSet.getInt("id"),
                                email: resultSet.getString("email"),
                                password: resultSet.getString("password"),
                                name: resultSet.getString("name"),
                                description: resultSet.getString("description"),
                                cpf: resultSet.getString("cpf"),
                                birthday: resultSet.getDate("birthday"),
                                country: resultSet.getString("country"),
                                state: resultSet.getString("state"),
                                postalCode: resultSet.getString("postalCode"),
                                skills: resultSet.getString("skills")?.replaceAll(/[{}]/, '')?.split(',')?.toList() ?: []
                        ]
                        return new NewCandidate(params)
                    } else {
                        return null
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace()
            return null
        }
    }

    boolean updateCandidate(NewCandidate original, NewCandidate updated) {
        if (!original || !updated || !updated.isAllSet()) {
            return false
        }

        if (!this.hasDifferences(original, updated)) {
            return false // Nenhuma alteração necessária
        }

        boolean originalAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
            connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.updateUsersTable(original, updated))
                if(!this.getPostalCodeId(updated)){
                    statement.execute(Queries.updatePostalCodesTable(original, updated))
                }
                statement.execute(Queries.updateCandidatesTable(original, updated))
                statement.execute(Queries.deleteUnusedPostalCodes())
                statement.execute(Queries.updateCandidateSkillTable(original, updated))
            }

            connection.commit()
            return true

        } catch (SQLException e) {
            connection.rollback()
            e.printStackTrace()
            return false
        } finally {
            connection.autoCommit = originalAutoCommit
        }
    }

    boolean deleteCandidateById(int id) {
        try {
            this.connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.deleteCandidateById(id))
                statement.execute(Queries.deleteUnusedPostalCodes())
            }
            return true
        } catch (SQLException e) {
            e.printStackTrace()
            return false
        }
    }

    //CRUD Enterprises
    boolean saveNewEnterprise(NewEnterprise enterprise) {
        if (!enterprise.isAllSet()) {
            return false
        }

        // Desativa o auto-commit para controle manual
        boolean originalAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
            // Executa a query completa (já inclui BEGIN e COMMIT)
            connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.insertUsersTable(enterprise))
                if(!this.getPostalCodeId(enterprise)){
                    statement.execute(Queries.insertPostalCodesTable(enterprise))
                }
                statement.execute(Queries.insertEnterprisesTable(enterprise))
            }

            connection.commit() // Confirma transação
            return true

        } catch (SQLException e) {
            connection.rollback() // Reverte em caso de erro
            e.printStackTrace()
            return false
        } finally {
            connection.autoCommit = originalAutoCommit // Restaura o auto-commit original
        }
    }

    NewEnterprise getEnterpriseById(int id) {
        try {
            // Usa withCloseable para fechar automaticamente Statement e ResultSet
            return this.connection.createStatement().withCloseable { statement ->
                statement.executeQuery(Queries.selectEnterpriseById(id)).withCloseable { resultSet ->
                    if (resultSet.next()) {
                        // Mapeia os dados (com tratamento de valores nulos)
                        Map params = [
                                id: resultSet.getInt("id"),
                                email: resultSet.getString("email"),
                                password: resultSet.getString("password"),
                                name: resultSet.getString("name"),
                                description: resultSet.getString("description"),
                                cnpj: resultSet.getString("cnpj"),
                                country: resultSet.getString("country"),
                                state: resultSet.getString("state"),
                                postalCode: resultSet.getString("postalCode")
                        ]
                        return new NewEnterprise(params)
                    } else {
                        return null
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace()
            return null
        }
    }

    boolean updateEnterprise(NewEnterprise original, NewEnterprise updated) {
        if (!original || !updated || !updated.isAllSet()) {
            return false
        }

        if (!this.hasDifferences(original, updated)) {
            return false // Nenhuma alteração necessária
        }

        boolean originalAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
            connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.updateUsersTable(original, updated))
                if(!this.getPostalCodeId(updated)){
                    statement.execute(Queries.updatePostalCodesTable(original, updated))
                }
                statement.execute(Queries.updateEnterprisesTable(original, updated))
                statement.execute(Queries.deleteUnusedPostalCodes())
            }

            connection.commit()
            return true

        } catch (SQLException e) {
            connection.rollback()
            e.printStackTrace()
            return false
        } finally {
            connection.autoCommit = originalAutoCommit
        }
    }

    boolean deleteEnterpriseById(int id) {
        try {
            this.connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.deleteEnterpriseById(id))
                statement.execute(Queries.deleteUnusedPostalCodes())
            }
            return true
        } catch (SQLException e) {
            e.printStackTrace()
            return false
        }
    }

    //CRUD Employments
    boolean saveNewEmployment(Employment employment) {
        if (!employment.isAllSet()) {
            return false
        }

        // Desativa o auto-commit para controle manual
        boolean originalAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
             //Executa a query completa (já inclui BEGIN e COMMIT)
            connection.createStatement().withCloseable { statement ->
                if(!this.getPostalCodeId(employment)){
                    statement.execute(Queries.insertPostalCodesTable(employment))
                }
                statement.execute(Queries.insertEmploymentsTable(employment))
                statement.execute(Queries.insertEmploymentSkillTable(employment))
            }

            connection.commit() // Confirma transação
            return true

        } catch (SQLException e) {
            connection.rollback() // Reverte em caso de erro
            e.printStackTrace()
            return false
        } finally {
            connection.autoCommit = originalAutoCommit // Restaura o auto-commit original
        }
    }

    Employment getEmploymentById(int id) {
        try {
            // Usa withCloseable para fechar automaticamente Statement e ResultSet
            return this.connection.createStatement().withCloseable { statement ->
                statement.executeQuery(Queries.selectEmploymentById(id)).withCloseable { resultSet ->
                    if (resultSet.next()) {
                        // Mapeia os dados (com tratamento de valores nulos)
                        Map params = [
                                id: resultSet.getInt("id"),
                                enterpriseId: resultSet.getInt("enterpriseId"),
                                name: resultSet.getString("name"),
                                description: resultSet.getString("description"),
                                country: resultSet.getString("country"),
                                state: resultSet.getString("state"),
                                postalCode: resultSet.getString("postalCode"),
                                skills: resultSet.getString("skills")?.replaceAll(/[{}]/, '')?.split(',')?.toList() ?: []
                        ]
                        return new Employment(params)
                    } else {
                        return null
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace()
            return null
        }
    }

    boolean updateEmployment(Employment original, Employment updated) {
        if (!original || !updated || !updated.isAllSet()) {
            return false
        }

        if (!this.hasDifferences(original, updated)) {
            return false // Nenhuma alteração necessária
        }

        boolean originalAutoCommit = connection.autoCommit
        connection.autoCommit = false

        try {
            connection.createStatement().withCloseable { statement ->
                if(!this.getPostalCodeId(updated)){
                    statement.execute(Queries.updatePostalCodesTable(original, updated))
                }
                statement.execute(Queries.updateEmploymentsTable(original, updated))
                statement.execute(Queries.deleteUnusedPostalCodes())
                statement.execute(Queries.updateEmploymentSkillTable(original, updated))
            }

            connection.commit()
            return true

        } catch (SQLException e) {
            connection.rollback()
            e.printStackTrace()
            return false
        } finally {
            connection.autoCommit = originalAutoCommit
        }
    }

    boolean deleteEmploymentById(int id) {
        try {
            this.connection.createStatement().withCloseable { statement ->
                statement.execute(Queries.deleteEmploymentById(id))
                statement.execute(Queries.deleteUnusedPostalCodes())
            }
            return true
        } catch (SQLException e) {
            e.printStackTrace()
            return false
        }
    }


    //UTILS
    Integer getUserIdByEmail(String email) {
        try {
            return this.connection.createStatement().withCloseable { statement ->
                statement.executeQuery(Queries.selectIdByEmail(email)).withCloseable { resultSet ->
                    if (resultSet.next()) {
                        return resultSet.getInt("id")
                    } else {
                        return null
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace()
            return null
        }
    }

    Integer getPostalCodeId(update) {
        try {
            // Usa withCloseable para fechar automaticamente Statement e ResultSet
            return this.connection.createStatement().withCloseable { statement ->
                statement.executeQuery(Queries.selectPostalCodeId(update.getPostalCode(), update.getState())).withCloseable { resultSet ->
                    if (resultSet.next()) {
                        return resultSet.getInt("id")
                    } else {
                        return null
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace()
            return null
        }
    }

    Integer getEmploymentId(enterpriseId, employmentName) {
        try {
            // Usa withCloseable para fechar automaticamente Statement e ResultSet
            return this.connection.createStatement().withCloseable { statement ->
                statement.executeQuery(Queries.selectEmploymentId(enterpriseId, employmentName)).withCloseable { resultSet ->
                    if (resultSet.next()) {
                        return resultSet.getInt("id")
                    } else {
                        return null
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace()
            return null
        }
    }

    boolean hasDifferences(entity1, entity2, boolean ignoreId = false) {
        return entity1.properties.any { key, value ->
            // Ignora 'class' e (opcionalmente) 'id'
            boolean shouldIgnore = key == 'class' || (ignoreId && key == 'id')

            // Verifica diferenças apenas nas propriedades não ignoradas
            !shouldIgnore && entity2.properties[key] != value
        }
    }
}


