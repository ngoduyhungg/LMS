import EmptyCustom from '@/shared/components/empty/EmptyCustom';
import StudentInfoForm from './StudentInfoForm';
import TeacherInfoForm from './TeacherInfoForm';
import { useAppSelector } from '@/app/redux/hooks';
import { USER_ROLE } from '../types/user-role-type';

const RoleInfoTab = () => {
  const { user } = useAppSelector((state) => state.auth);

  switch (user?.role) {
    case USER_ROLE.STUDENT:
      if (!user.student) return null;
      return <StudentInfoForm student={user.student} />;

    case USER_ROLE.TEACHER:
      if (!user.teacher) return null;
      return <TeacherInfoForm teacher={user.teacher} />;

    default:
      return <EmptyCustom title="Không có thông tin bổ sung" />;
  }
};

export default RoleInfoTab;
