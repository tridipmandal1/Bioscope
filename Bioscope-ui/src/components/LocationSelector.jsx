/* eslint-disable no-unused-vars */
import React from "react";

const LocationSelector = () => {
  return (
    <div className="flex items-center cursor-pointer">
      <select className="bg-transparent text-gray-700 font-semibold focus:outline-none">
        <option>Kolkata</option>
        <option>Mumbai</option>
        <option>Delhi</option>
        <option>Bangalore</option>
      </select>
    </div>
  );
};

export default LocationSelector;
