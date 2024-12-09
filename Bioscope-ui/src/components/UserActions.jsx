// eslint-disable-next-line no-unused-vars
import React from "react";
// import { UserCircleIcon } from "@heroicons/react/24/outline";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";

const UserActions = () => {
  return (
    <div className="flex items-center space-x-4">
      <button className="flex items-center space-x-1 text-gray-700 hover:text-blue-600">
        {/* <UserCircleIcon className="h-6 w-6" /> */}
        <AccountCircleIcon className="h-6 w-6" />
        <span>Login</span>
      </button>
      <button className="px-4 py-1 text-sm bg-blue-600 text-white rounded-md hover:bg-blue-700">
        Sign Up
      </button>
    </div>
  );
};

export default UserActions;
