import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { AnimalDetailComponent } from './features/animal-detail/animal-detail.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';
import { AdopterLayoutComponent } from './features/adopter/adopter-layout.component';
import { AdopterProfileComponent } from './features/adopter/profile/adopter-profile.component';
import { AdopterQuestionnaireComponent } from './features/adopter/questionnaire/adopter-questionnaire.component';
import { AdopterApplicationsListComponent } from './features/adopter/applications-list/adopter-applications-list.component';
import { AdopterApplicationDetailComponent } from './features/adopter/application-detail/adopter-application-detail.component';
import { AdopterApplicationCreateComponent } from './features/adopter/application-create/adopter-application-create.component';
import { AdopterRecommendationsComponent } from './features/adopter/recommendations/adopter-recommendations.component';
import { StaffLayoutComponent } from './features/staff/staff-layout.component';
import { StaffDashboardComponent } from './features/staff/dashboard/staff-dashboard.component';
import { StaffAnimalsListComponent } from './features/staff/animals-list/staff-animals-list.component';
import { StaffAnimalFormComponent } from './features/staff/animal-form/staff-animal-form.component';
import { StaffAnimalEditComponent } from './features/staff/animal-edit/staff-animal-edit.component';
import { StaffAnimalMoveComponent } from './features/staff/animal-move/staff-animal-move.component';
import { StaffApplicationsListComponent } from './features/staff/applications-list/staff-applications-list.component';
import { StaffApplicationDetailComponent } from './features/staff/application-detail/staff-application-detail.component';
import { StaffEmployeesListComponent } from './features/staff/employees-list/staff-employees-list.component';
import { StaffEmployeeDetailComponent } from './features/staff/employee-detail/staff-employee-detail.component';
import { StaffEmployeeFormComponent } from './features/staff/employee-form/staff-employee-form.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'animals/:id', component: AnimalDetailComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  {
    path: 'adopter',
    component: AdopterLayoutComponent,
    canActivate: [authGuard, roleGuard(['ADOPTER'])],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'applications' },
      { path: 'profile', component: AdopterProfileComponent },
      { path: 'questionnaire', component: AdopterQuestionnaireComponent },
      { path: 'recommendations', component: AdopterRecommendationsComponent },
      { path: 'applications', component: AdopterApplicationsListComponent },
      { path: 'applications/new', component: AdopterApplicationCreateComponent },
      { path: 'applications/:id', component: AdopterApplicationDetailComponent }
    ]
  },

  {
    path: 'staff',
    component: StaffLayoutComponent,
    canActivate: [authGuard, roleGuard(['STAFF'])],
    children: [
      { path: '', pathMatch: 'full', component: StaffDashboardComponent },
      { path: 'animals', component: StaffAnimalsListComponent },
      { path: 'animals/new', component: StaffAnimalFormComponent },
      { path: 'animals/:id/edit', component: StaffAnimalEditComponent },
      { path: 'animals/:id/move', component: StaffAnimalMoveComponent },
      { path: 'applications', component: StaffApplicationsListComponent },
      { path: 'applications/:id', component: StaffApplicationDetailComponent },
      { path: 'employees', component: StaffEmployeesListComponent },
      { path: 'employees/new', component: StaffEmployeeFormComponent },
      { path: 'employees/:id', component: StaffEmployeeDetailComponent }
    ]
  }
];
