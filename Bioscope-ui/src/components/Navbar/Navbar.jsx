/* eslint-disable no-unused-vars */
import React, { useEffect, useState } from "react";
import SearchBar from "../SearchBar";
import LocationSelector from "../LocationSelector";
import NavLinks from "../NavLinks";
import UserActions from "../UserActions";
import Headroom from "react-headroom";

const Navbar = () => {
//   const[isSticky,setIsSticky]=
//   useState<Boolean>(false);
//   useEffect(() =>{
//     const handleScroll =()=>{
//       const isTop=window.scrollY<40;
//       setIsSticky(!isTop);
//     };
//     window.addEventListener('scroll',handleScroll);
//     return() =>{
//       window.removeEventListener('scroll',)
//     }
//   })

  return (
    <Headroom>  {/*this one temporary use for sticky navbar i will change it latter*/}
    <div>
      
      <nav
        // className={`$isSticky ?'sticky bg-customBlue h-20 px-10 py-2 font-serif text-white flex justify-start items-center mr-2 ml-2 rounded-xl mt-0 overflow-hidden fixed-top`}
        className="bg-customBlue h-20 px-10 py-2 font-serif text-white flex justify-start items-center mr-2 ml-2 rounded-xl mt-0 overflow-hidden fixed-top">
        <NavLinks />
        <SearchBar />
        <LocationSelector />
        <UserActions />
      </nav>
    </div>
    </Headroom>
  );
};

export default Navbar;
