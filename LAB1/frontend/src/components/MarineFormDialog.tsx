import React from "react";
import { useForm } from "react-hook-form";
import type { SpaceMarineDto } from "../api/spaceMarineApi";

interface Props {
  open: boolean;
  initial?: SpaceMarineDto;
  onSubmit: (dto: SpaceMarineDto) => void;
  onClose: () => void;
}

export const MarineFormDialog: React.FC<Props> = ({
  open,
  initial,
  onSubmit,
  onClose,
}) => {
  const { register, handleSubmit, reset } = useForm<SpaceMarineDto>({
    defaultValues: initial || {
      name: "",
      coordinates: { x: 0, y: 0 },
      chapterId: 1,
      health: 1,
      achievements: "",
      height: 1.7,
      weaponType: "BOLT_PISTOL",
    },
  });

  React.useEffect(() => {
    reset(initial);
  }, [initial, reset]);

  if (!open) return null;

  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/40">
      <div className="bg-white rounded-xl p-4 w-[400px]">
        <h2 className="text-xl font-bold mb-3">
          {initial ? "Edit Marine" : "New Marine"}
        </h2>
        <form
          onSubmit={handleSubmit((data) => {
            onSubmit(data);
            onClose();
          })}
          className="flex flex-col gap-2"
        >
          <input {...register("name")} placeholder="Name" className="border p-1" />
          <input
            type="number"
            {...register("coordinates.x")}
            placeholder="X"
            className="border p-1"
          />
          <input
            type="number"
            {...register("coordinates.y")}
            placeholder="Y"
            className="border p-1"
          />
          <input
            type="number"
            {...register("health")}
            placeholder="Health"
            className="border p-1"
          />
          <input
            {...register("achievements")}
            placeholder="Achievements"
            className="border p-1"
          />
          <input
            type="number"
            step="0.1"
            {...register("height")}
            placeholder="Height"
            className="border p-1"
          />
          <select {...register("weaponType")} className="border p-1">
            <option value="HEAVY_BOLTGUN">HEAVY_BOLTGUN</option>
            <option value="BOLT_PISTOL">BOLT_PISTOL</option>
            <option value="PLASMA_GUN">PLASMA_GUN</option>
            <option value="HEAVY_FLAMER">HEAVY_FLAMER</option>
          </select>
          <button
            type="submit"
            className="bg-blue-600 text-white py-1 rounded mt-2"
          >
            Save
          </button>
        </form>
        <button
          onClick={onClose}
          className="absolute top-2 right-3 text-gray-500"
        >
          ✕
        </button>
      </div>
    </div>
  );
};
