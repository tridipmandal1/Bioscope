// eslint-disable-next-line no-unused-vars
import React from "react";
import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import "swiper/css/autoplay";

// Import required Swiper modules
import { Pagination, Navigation, Autoplay } from "swiper/modules";

export default function App() {
  return (
    <div className="h-screen bg-gray-200">
      <Swiper
        slidesPerView={1}
        spaceBetween={30}
        loop={true}
        pagination={{
          clickable: true,
        }}
        navigation={true}
        autoplay={{
          delay: 5000, // Delay in milliseconds (5 seconds)
          disableOnInteraction: false, // Keeps autoplay running even after user interaction
        }}
        modules={[Pagination, Navigation, Autoplay]}
        className="w-full h-80 mx-auto"
      >
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            <img
              src="https://images.pexels.com/photos/1961778/pexels-photo-1961778.jpeg?auto=compress&cs=tinysrgb&w=600"
              alt="img"
              className="h-full w-full object-fill"
            />
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 2
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 3
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 4
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-blue-500 text-lg font-semibold">
            Slide 5
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 6
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 7
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 8
          </div>
        </SwiperSlide>
        <SwiperSlide>
          <div className="flex items-center justify-center h-full bg-white text-lg font-semibold">
            Slide 9
          </div>
        </SwiperSlide>
      </Swiper>
    </div>
  );
}
