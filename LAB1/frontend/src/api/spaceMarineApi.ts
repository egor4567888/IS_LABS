import { api } from "./client";

export interface SpaceMarineDto {
  id?: number;
  name: string;
  coordinates: { x: number; y: number };
  creationDate?: string;
  chapterId: number;
  health: number;
  achievements: string;
  height: number;
  weaponType?: string;
}

export const spaceMarineApi = {
  async list(params?: any) {
    const res = await api.get("/space-marines", { params });
    return res.data;
  },
  async get(id: number) {
    const res = await api.get(`/space-marines/${id}`);
    return res.data;
  },
  async create(dto: SpaceMarineDto) {
    const res = await api.post("/space-marines", dto);
    return res.data;
  },
  async update(id: number, dto: SpaceMarineDto) {
    const res = await api.put(`/space-marines/${id}`, dto);
    return res.data;
  },
  async remove(id: number) {
    await api.delete(`/space-marines/${id}`);
  },
};
