// eslint-disable-next-line no-unused-vars
import React from "react";
import HomeIcon from "@mui/icons-material/Home";
import MovieIcon from "@mui/icons-material/Movie";
import ScheduleIcon from "@mui/icons-material/Schedule";
import PercentIcon from "@mui/icons-material/Percent";
import MovingIcon from "@mui/icons-material/Moving";
const NavLinks = () => {
  const links = [
    { name: "Home", icon: <HomeIcon /> },
    { name: "Showtimings", icon: <ScheduleIcon /> },
    { name: "Cinemas", icon: <MovieIcon /> },
    { name: "Offers", icon: <PercentIcon /> },
    { name: "Investor Section", icon: <MovingIcon /> },
  ];

  return (
    <div className="flex space-x-6">
      {links.map((link) => (
        <a
          key={link.name}
          href="#"
          className="flex items-center text-gray-700 hover:text-blue-600"
        >
          <span className="mr-1">{link.icon}</span>
          <span>{link.name}</span>
        </a>
      ))}
    </div>
  );
};

export default NavLinks;
