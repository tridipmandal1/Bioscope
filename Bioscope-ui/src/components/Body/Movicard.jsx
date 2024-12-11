// eslint-disable-next-line no-unused-vars
import React from "react";
import FavoriteIcon from "@mui/icons-material/Favorite";

const Movie = ({ title, poster, likes, rating, votes, genres }) => {
  return (
    <div className="w-[240px] overflow-hidden bg-black text-white rounded-lg shadow-md">
      <div className="relative h-[360px]">
        <img src={poster} alt={title} className="h-full w-full object-cover" />
        <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black to-transparent p-4">
          {likes && (
            <div className="flex items-center gap-1">
              <FavoriteIcon className="h-4 w-4 fill-green-500 text-green-500" />
              <span className="text-sm">{likes} Likes</span>
            </div>
          )}
          {rating && (
            <div className="flex items-center gap-1">
              <span className="text-sm text-red-500">{rating}/10</span>
              <span className="text-sm text-gray-400">{votes}K Votes</span>
            </div>
          )}
        </div>
      </div>
      <div className="p-3">
        <h3 className="font-semibold leading-tight">{title}</h3>
        <p className="mt-1 text-sm text-gray-400">{genres.join("/")}</p>
      </div>
    </div>
  );
};

export default Movie;
