import { createBrowserRouter } from 'react-router-dom';

import AuthLayout from '../layouts/AuthLayout';
import MainLayout from '@/app/layouts/MainLayout';
import ProtectedRoute from './ProtectedRoute';

import LoginPage from '@/features/auth/pages/LoginPage';
import RegisterPage from '@/features/auth/pages/RegisterPage';
import DashboardPage from '@/features/dashboard/pages/DashboardPage';
import StudentPage from '@/features/students/pages/StudentPage';
import UserProfilePage from '@/features/users/pages/UserProfilePage';
import TeacherPage from '@/features/teachers/pages/TeacherPage';
import CoursePage from '@/features/courses/pages/CoursePage';
import EnrollmentPage from '@/features/enrollments/pages/EnrollmentPage';
import ParentPage from '@/features/parents/pages/ParentPage';
import UserPage from '@/features/users/pages/UserPage';
import RoomPage from '@/features/rooms/pages/RoomPage';
import SchedulePage from '@/features/schedule/pages/SchedulePage';
import CourseClassPage from '@/features/course-class/pages/CourseClassPage';
import CourseClassSessionPage from '@/features/course-class-session/pages/CourseClassSessionPage';
import CourseClassCalendarPage from '@/features/course-class-session/pages/CourseClassCalendarPage';
import LeaveRequestPage from '@/features/leave-request/pages/LeaveRequestPage';
import TuitionInvoicePage from '@/features/tuition-invoice/pages/TuitionInvoicePage';
import PromotionPage from '@/features/promotion/pages/PromotionPage';
import PaymentPage from '@/features/payment/pages/PaymentPage';
import CourseOverviewPage from '@/features/courses/pages/CourseOverviewPage';

export const router = createBrowserRouter([
  /******************** AUTH *********************/
  {
    element: <ProtectedRoute requireAuth={false} />,
    children: [
      {
        path: '/auth',
        element: <AuthLayout />,
        children: [
          { path: 'login', element: <LoginPage /> },
          { path: 'register', element: <RegisterPage /> },
        ],
      },
    ],
  },

  /******************** MAIN *********************/
  {
    element: <ProtectedRoute />,
    children: [
      {
        path: '/',
        element: <MainLayout />,
        children: [
          { index: true, element: <DashboardPage /> },
          { path: 'profile', element: <UserProfilePage /> },
          { path: 'students', element: <StudentPage /> },
          { path: 'teachers', element: <TeacherPage /> },
          { path: 'parents', element: <ParentPage /> },
          { path: 'accounts', element: <UserPage /> },
          
          /* --- KHU VỰC ĐỒNG BỘ ĐƯỜNG DẪN COURSE DOMAIN FOUNDATION --- */
          { path: 'courses', element: <CoursePage /> },
          { path: 'my-courses', element: <CoursePage /> },
          { path: 'course-management', element: <CoursePage /> },
          
          { path: 'rooms', element: <RoomPage /> },
          { path: 'schedules', element: <SchedulePage /> },
          { path: 'course-classes', element: <CourseClassPage /> },
          { path: 'course-class-sessions', element: <CourseClassSessionPage /> },
          { path: 'calendar', element: <CourseClassCalendarPage /> },
          { path: 'enrollments', element: <EnrollmentPage /> },
          { path: 'leave-requests', element: <LeaveRequestPage /> },
          { path: 'tuition-invoices', element: <TuitionInvoicePage /> },
          { path: 'payments', element: <PaymentPage /> },
          { path: 'promotions', element: <PromotionPage /> },
          { path: 'courses', element: <CoursePage /> },
          { path: 'courses/:id', element: <CourseOverviewPage /> },
          { path: 'my-courses', element: <CoursePage /> },
          { path: 'my-courses/:id', element: <CourseOverviewPage /> },
          { path: 'course-management', element: <CoursePage /> },
          { path: 'course-management/:id', element: <CourseOverviewPage /> },
        ],
      },
    ],
  },
]);