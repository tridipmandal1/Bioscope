// eslint-disable-next-line no-unused-vars
import React from "react";
// import { MagnifyingGlassIcon } from "@heroicons/react/24/outline";
import SearchIcon from "@mui/icons-material/Search";

const SearchBar = () => {
  return (
    <div className="relative flex-1 max-w-xl">
      <input
        type="text"
        placeholder="Search for Movies, Events, Plays, Sports and Activities"
        className="w-96 px-4 py-2 pl-10 text-sm bg-white rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 border-red-300 ml-2 mr-2"
      />
      {/* <MagnifyingGlassIcon className="absolute left-3 top-2.5 h-5 w-5 text-gray-400" /> */}
      <SearchIcon className="absolute left-3 top-2 h-6 w-5 text-gray-400" />
    </div>
  );
};

export default SearchBar;
