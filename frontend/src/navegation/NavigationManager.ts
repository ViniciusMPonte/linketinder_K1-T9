import {Candidate, CandidateConfig} from "../entities/Candidate"
import {Enterprise, EnterpriseConfig} from "../entities/Enterprise"
import {Employment, EmploymentConfig} from "../entities/Employment"

import DatabaseManager from "../data/DatabaseManager"

const dbManager = new DatabaseManager()
import LoginManager from "../services/LoginManager";

const loginManager = new LoginManager()

import Chart from "../components/Chart";
import Card from "../components/Card";


export default class NavigationManager {

    router(): void {
        const path = window.location.pathname;
        switch (path) {
            case '/candidate/register-candidate.html':
                this.activeCandidateCreateFormListener()
                break;
            case '/enterprise/register-enterprise.html':
                this.activeEnterpriseCreateFormListener()
                break;
            case '/enterprise/register-employment.html':
                this.activeEmploymentCreateFormListener()
                break;
            case '/candidate/login-candidate.html':
                this.activeCandidateLoginFormListener()
                break;
            case '/enterprise/login-enterprise.html':
                this.activeEnterpriseLoginFormListener()
                break;
            case '/enterprise/candidates-list.html':
                this.buildEnterpriseCandidatesList()
                break;
            case '/candidate/employments-list.html':
                this.buildCandidateEmploymentsList()
                break;
            default:
                console.log('Rota não encontrada: Página 404');
                break;
        }
    }

    innerHTMLInject(tag: HTMLElement | null, output: string): void {
        if (tag) {
            tag.innerHTML += output
        }
    }

    activeCandidateCreateFormListener() {

        const form = document.querySelector('.card-body')
        if (!form) return
        const formBtn = document.querySelector('.card-body #create-candidate-btn')
        if (!formBtn) return

        formBtn.addEventListener('click', (event) => {
            event.preventDefault()
            const newCandidateData: CandidateConfig = {
                name: (document.getElementById('candidate-name-input') as HTMLInputElement)?.value || '',
                email: (document.getElementById('candidate-email-input') as HTMLInputElement)?.value || '',
                password: (document.getElementById('candidate-password-input') as HTMLInputElement)?.value || '',
                country: (document.getElementById('candidate-country-input') as HTMLInputElement)?.value || '',
                state: (document.getElementById('candidate-state-input') as HTMLInputElement)?.value || '',
                cep: (document.getElementById('candidate-cep-input') as HTMLInputElement)?.value || '',
                skills: (document.getElementById('candidate-skills-input') as HTMLInputElement)?.value?.split(', ') || [],
                description: (document.getElementById('candidate-description-input') as HTMLInputElement)?.value || '',
                cpf: (document.getElementById('candidate-cpf-input') as HTMLInputElement)?.value || '',
                age: Number((document.getElementById('candidate-age-input') as HTMLInputElement)?.value) || 0,
            }

            const isEmptyField = Object.values(newCandidateData).some(value => value.toString().trim() === '');

            if (isEmptyField) {
                alert('Por favor, preencha todos os campos obrigatórios!');
                return;
            }

            let candidatesWithSameEmail = dbManager.candidates?.filter(candidate =>
                candidate.email == newCandidateData.email
            )

            if (candidatesWithSameEmail != undefined && candidatesWithSameEmail.length == 0) {
                dbManager.addCandidate(new Candidate(newCandidateData))
                alert('Cadastro realizado com sucesso!')
                window.location.href = '/candidate/login-candidate.html';
            } else if (candidatesWithSameEmail != undefined && candidatesWithSameEmail.length > 0) {
                alert('Já existe um usuário com mesmo e-mail.')
            }

        })

    }

    activeEnterpriseCreateFormListener() {

        const form = document.querySelector('.card-body')
        if (!form) return
        const formBtn = document.querySelector('.card-body #create-enterprise-btn')
        if (!formBtn) return

        formBtn.addEventListener('click', (event) => {
            event.preventDefault()
            const newEnterpriseData: EnterpriseConfig = {
                name: (document.getElementById('enterprise-name-input') as HTMLInputElement)?.value || '',
                email: (document.getElementById('enterprise-email-input') as HTMLInputElement)?.value || '',
                password: (document.getElementById('enterprise-password-input') as HTMLInputElement)?.value || '',
                country: (document.getElementById('enterprise-country-input') as HTMLInputElement)?.value || '',
                state: (document.getElementById('enterprise-state-input') as HTMLInputElement)?.value || '',
                cep: (document.getElementById('enterprise-cep-input') as HTMLInputElement)?.value || '',
                description: (document.getElementById('enterprise-description-input') as HTMLInputElement)?.value || '',
                cnpj: (document.getElementById('enterprise-cnpj-input') as HTMLInputElement)?.value || '',
            }

            const isEmptyField = Object.values(newEnterpriseData).some(value => value.toString().trim() === '');

            if (isEmptyField) {
                alert('Por favor, preencha todos os campos obrigatórios!');
                return;
            }

            let enterprisesWithSameEmail = dbManager.enterprises?.filter(enterprise =>
                enterprise.email == newEnterpriseData.email
            )

            if (enterprisesWithSameEmail != undefined && enterprisesWithSameEmail.length == 0) {
                dbManager.addEnterprise(new Enterprise(newEnterpriseData))
                alert('Cadastro realizado com sucesso!')
                window.location.href = '/enterprise/login-enterprise.html';
            } else if (enterprisesWithSameEmail != undefined && enterprisesWithSameEmail.length > 0) {
                alert('Já existe um usuário com mesmo e-mail.')
            }
        })

    }

    activeEmploymentCreateFormListener() {

        const form = document.querySelector('.card-body')
        if (!form) return
        const formBtn = document.querySelector('.card-body #create-employment-btn')
        if (!formBtn) return

        formBtn.addEventListener('click', (event) => {
            event.preventDefault()
            const newEmploymentData: EmploymentConfig = {
                name: (document.getElementById('employment-name-input') as HTMLInputElement)?.value || '',
                description: (document.getElementById('employment-description-input') as HTMLInputElement)?.value || '',
                skills: (document.getElementById('employment-skills-input') as HTMLInputElement)?.value?.split(', ') || [],
            }

            dbManager.addEmployment(new Employment(newEmploymentData))

            alert('Cadastro realizado com sucesso!')
        })

    }

    activeCandidateLoginFormListener() {

        const form = document.querySelector('.card-body')
        if (!form) return
        const formBtn = document.querySelector('.card-body #login-candidate-btn')
        if (!formBtn) return

        formBtn.addEventListener('click', (event) => {
            event.preventDefault()
            const loginCandidateData: CandidateConfig = {
                email: (document.getElementById('candidate-email-input') as HTMLInputElement)?.value || '',
                password: (document.getElementById('candidate-password-input') as HTMLInputElement)?.value || '',
            }

            let candidatesFiltered = dbManager.candidates?.filter(candidate =>
                candidate.email == loginCandidateData.email && candidate.password == loginCandidateData.password
            )

            if (candidatesFiltered == undefined || candidatesFiltered.length == 0) {
                alert('Usuário não encontrado')
            } else if (candidatesFiltered.length > 1) {
                alert('Usuário repetido')
            } else if (candidatesFiltered.length == 1) {
                loginManager.logIn(candidatesFiltered[0])
                window.location.href = '/candidate/employments-list.html';
            }
        })
    }

    activeEnterpriseLoginFormListener() {

        const form = document.querySelector('.card-body')
        if (!form) return
        const formBtn = document.querySelector('.card-body #login-enterprise-btn')
        if (!formBtn) return

        formBtn.addEventListener('click', (event) => {
            event.preventDefault()
            const loginEnterpriseData: EnterpriseConfig = {
                email: (document.getElementById('enterprise-email-input') as HTMLInputElement)?.value || '',
                password: (document.getElementById('enterprise-password-input') as HTMLInputElement)?.value || '',
            }

            let enterprisesFiltered = dbManager.enterprises?.filter(enterprise =>
                enterprise.email == loginEnterpriseData.email && enterprise.password == loginEnterpriseData.password
            )

            if (enterprisesFiltered == undefined || enterprisesFiltered.length == 0) {
                alert('Usuário não encontrado')
            } else if (enterprisesFiltered.length > 1) {
                alert('Usuário repetido')
            } else if (enterprisesFiltered.length == 1) {
                loginManager.logIn(enterprisesFiltered[0])
                window.location.href = '/enterprise/candidates-list.html';
            }
        })
    }

    buildEnterpriseCandidatesList() {
        let chartTag = document.getElementById('myChart') as HTMLCanvasElement
        if (chartTag) {
            if (dbManager.candidates == null) return
            let skillCounts = Chart.countCandidateSkills(dbManager.candidates)
            let keys = Object.keys(skillCounts)
            let values = Object.values(skillCounts)
            Chart.build(chartTag, keys, values);
        }

        if (dbManager.candidates == null) return
        dbManager.candidates.forEach(candidate => {
                const cardComponent = new Card(candidate.params, 'candidate');
                this.innerHTMLInject(document.querySelector('#candidates-list'), cardComponent.getCard());
            }
        )
    }

    buildCandidateEmploymentsList() {
        if (dbManager.employments == null) return
        dbManager.employments.forEach(employment => {
                const cardComponent = new Card(employment.params, 'employment', employment.enterpriseId);
                this.innerHTMLInject(document.querySelector('#employments-list'), cardComponent.getCard());
            }
        )
    }
}