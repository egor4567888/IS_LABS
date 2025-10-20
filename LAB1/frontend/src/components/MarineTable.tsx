import React from "react";
import type { SpaceMarineDto } from "../api/spaceMarineApi";


interface Props {
  marines: SpaceMarineDto[];
  onEdit: (m: SpaceMarineDto) => void;
  onDelete: (id: number) => void;
}

const MarineTable: React.FC<Props> = ({ marines, onEdit, onDelete }) => (
  <table className="min-w-full border border-gray-300 shadow-sm">
    <thead className="bg-gray-100">
      <tr>
        <th className="p-2 border">ID</th>
        <th className="p-2 border">Name</th>
        <th className="p-2 border">Health</th>
        <th className="p-2 border">Achievements</th>
        <th className="p-2 border">Weapon</th>
        <th className="p-2 border">Actions</th>
      </tr>
    </thead>
    <tbody>
      {marines.map((m) => (
        <tr key={m.id} className="hover:bg-gray-50">
          <td className="p-2 border">{m.id}</td>
          <td className="p-2 border">{m.name}</td>
          <td className="p-2 border">{m.health}</td>
          <td className="p-2 border">{m.achievements}</td>
          <td className="p-2 border">{m.weaponType}</td>
          <td className="p-2 border text-center">
            <button
              className="px-2 py-1 text-blue-600 hover:underline"
              onClick={() => onEdit(m)}
            >
              Edit
            </button>
            <button
              className="px-2 py-1 text-red-600 hover:underline"
              onClick={() => onDelete(m.id!)}
            >
              Delete
            </button>
          </td>
        </tr>
      ))}
    </tbody>
  </table>
);

export default MarineTable;
