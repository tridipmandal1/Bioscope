/* eslint-disable no-unused-vars */
import React from "react";
import SearchBar from "../SearchBar";
import LocationSelector from "../LocationSelector";
import NavLinks from "../NavLinks";
import UserActions from "../UserActions";

const Navbar = () => {
  return (
    <div className="bg-customBlue h-20 px-10 py-2 font-serif text-white flex justify-start items-center mr-2 ml-2 rounded-xl mt-4 overflow-hidden">
      {/* <img
        src="https://media.licdn.com/dms/image/v2/D560BAQGapGhkW1UKmA/company-logo_200_200/company-logo_200_200/0/1720855644840?e=2147483647&v=beta&t=pKO4OvLf2KWQ_-fDQNK72Qx5rvyDUeDsZ_edKH0zOQs"
        alt="her is photo"
        className="h-12 w-12 md:h-12 md:w-16 lg:h-20 lg:w-20 object-contain rounded-3xl"
      />
      <h1 className="text-yellow-300 font-bold rounded-3xl bg-red-600 hover:text-white hover:bg-blue-800 cursor-pointer ml-1">
        Souvik Das
      </h1>
      <ul className="flex space-x-4 p-4 ml-1">
        <div className=" bg-red-500 rounded-md ">
          <li className="hover:text-blue-500 hover:bg-slate-600 hover:rounded-lg px-2 py-1 cursor-pointer text-yellow-400">
            Home
          </li>
        </div>
        <div className="bg-red-500 rounded-md ">
          <li className="hover:text-blue-500 cursor-pointer text-yellow-400">
            About
          </li>
        </div>
        <li className="hover:text-blue-500 cursor-pointer text-yellow-400">
          Contact
        </li>
        <li className="hover:text-blue-500 cursor-pointer text-yellow-400">
          mobile
        </li>
      </ul>
      <div className="mr-4 flex">
        <span>login</span>
      </div> */}
      <NavLinks />
      <SearchBar />
      <LocationSelector />
      <UserActions />
    </div>
  );
};

export default Navbar;
