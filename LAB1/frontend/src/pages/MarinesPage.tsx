import React, { useEffect, useState, useCallback } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { ChevronLeft, ChevronRight, Plus, Edit, Trash2, AlertCircle, Wifi, WifiOff } from "lucide-react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { webSocketService } from "@/services/websocketService";

// Типы для TypeScript
interface Coordinates {
  x: number;
  y: number;
}

interface SpaceMarine {
  id: number;
  name: string;
  coordinates: Coordinates;
  creationDate: string;
  chapterName?: string;
  chapterId?: number;
  health: number;
  achievements: string;
  height: number;
  weaponType?: string;
}

interface ApiResponse {
  content: SpaceMarine[];
  totalPages: number;
}

interface Chapter {
  id: number;
  name: string;
}

interface WebSocketMessage {
  action: 'create' | 'update' | 'delete';
  id: number;
}

// Форма для создания/редактирования
interface MarineFormData {
  name: string;
  coordinates: { x: number; y: number };
  chapterId: number;
  health: number;
  achievements: string;
  height: number;
  weaponType: string | null;
}

// Enum для типов оружия
const WeaponTypes = [
  "HEAVY_BOLTGUN",
  "BOLT_PISTOL", 
  "PLASMA_GUN",
  "HEAVY_FLAMER"
] as const;

export default function SpaceMarinesPage() {
  const [marines, setMarines] = useState<SpaceMarine[]>([]);
  const [chapters, setChapters] = useState<Chapter[]>([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState("id");
  const [filters, setFilters] = useState({
    name: "",
    achievements: "",
  });
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingMarine, setEditingMarine] = useState<SpaceMarine | null>(null);
  const [formData, setFormData] = useState<MarineFormData>({
    name: "",
    coordinates: { x: 0, y: 0 },
    chapterId: 0,
    health: 0,
    achievements: "",
    height: 0,
    weaponType: null,
  });
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [wsConnected, setWsConnected] = useState(false);

  const API_BASE_URL = 'http://localhost:24788';

  // Загрузка космодесантников
  const fetchMarines = useCallback(async () => {
    setLoading(true);
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy: sortBy,
    });

    if (filters.name) params.append("name", filters.name);
    if (filters.achievements) params.append("achievements", filters.achievements);

    try {
      console.log('Fetching marines with params:', params.toString());
      const res = await fetch(`${API_BASE_URL}/api/space-marines?${params.toString()}`);
      if (res.ok) {
        const data: ApiResponse = await res.json();
        console.log('Marines data received:', data);
        setMarines(data.content);
        setTotalPages(data.totalPages);
      } else {
        console.error('Failed to fetch marines:', res.status);
        showError('Ошибка загрузки данных');
      }
    } catch (error) {
      console.error('Error fetching marines:', error);
      showError('Ошибка загрузки данных');
    } finally {
      setLoading(false);
    }
  }, [page, size, sortBy, filters.name, filters.achievements]);

  // Загрузка глав
  const fetchChapters = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/chapters`);
      if (res.ok) {
        const data: Chapter[] = await res.json();
        setChapters(data);
        if (data.length > 0 && formData.chapterId === 0) {
          setFormData(prev => ({ ...prev, chapterId: data[0].id }));
        }
      }
    } catch (error) {
      console.error('Error fetching chapters:', error);
    }
  };

   // Обработчик WebSocket сообщений
   const handleWebSocketMessage = useCallback((message: WebSocketMessage) => {
    console.log('WebSocket message received in component:', message);
    showSuccess(`Изменение синхронизировано: ${message.action} ID ${message.id}`);
    
    // Перезагружаем данные при любом изменении
    fetchMarines();
  }, [fetchMarines]);

  // Обработчик изменения состояния подключения
  const handleConnectionChange = useCallback((connected: boolean) => {
    console.log('WebSocket connection status changed in component:', connected);
    setWsConnected(connected);
    if (connected) {
      showSuccess('WebSocket подключен - синхронизация активна');
    } else {
      showError('WebSocket отключен - синхронизация не работает');
    }
  }, []);

  // Инициализация WebSocket
  useEffect(() => {
    console.log('Initializing WebSocket...');
    
    // Подписка на изменения подключения
    webSocketService.onConnectionChange(handleConnectionChange);
    
    // Подписка на обновления космодесантников
    webSocketService.subscribe('/topic/spaceMarines', handleWebSocketMessage);
    
    // Подключаемся к WebSocket
    webSocketService.connect();

    return () => {
      console.log('Cleaning up WebSocket...');
      webSocketService.offConnectionChange(handleConnectionChange);
      webSocketService.unsubscribe('/topic/spaceMarines', handleWebSocketMessage);
    };
  }, [handleWebSocketMessage, handleConnectionChange]);

  useEffect(() => {
    fetchMarines();
    fetchChapters();
  }, [fetchMarines]);

  // Принудительное переподключение WebSocket
  const handleReconnect = () => {
    console.log('Manual WebSocket reconnect triggered');
    webSocketService.disconnect();
    setTimeout(() => webSocketService.connect(), 1000);
  };

  // Показать ошибку
  const showError = (message: string) => {
    setError(message);
    setTimeout(() => setError(null), 5000);
  };

  // Показать успех
  const showSuccess = (message: string) => {
    setSuccess(message);
    setTimeout(() => setSuccess(null), 3000);
  };

  // Обработчик сортировки
  const handleSort = (column: string) => {
    if (sortBy === column) {
      setSortBy(column + ",desc");
    } else {
      setSortBy(column);
    }
  };

  // Обработчик фильтров
  const handleFilterChange = (field: keyof typeof filters, value: string) => {
    setFilters({ ...filters, [field]: value });
  };

  const applyFilters = () => {
    setPage(0);
    fetchMarines();
  };

  // Открытие диалога для создания
  const handleCreate = () => {
    setEditingMarine(null);
    setFormData({
      name: "",
      coordinates: { x: 0, y: 0 },
      chapterId: chapters[0]?.id || 0,
      health: 0,
      achievements: "",
      height: 0,
      weaponType: null,
    });
    setError(null);
    setDialogOpen(true);
  };

  // Открытие диалога для редактирования
  const handleEdit = (marine: SpaceMarine) => {
    setEditingMarine(marine);
    setFormData({
      name: marine.name,
      coordinates: marine.coordinates,
      chapterId: marine.chapterId || chapters[0]?.id || 0,
      health: marine.health,
      achievements: marine.achievements,
      height: marine.height,
      weaponType: marine.weaponType || null,
    });
    setError(null);
    setDialogOpen(true);
  };

  // Удаление космодесантника
  const handleDelete = async (id: number) => {
    if (window.confirm('Вы уверены, что хотите удалить этого космодесантника?')) {
      try {
        const res = await fetch(`${API_BASE_URL}/api/space-marines/${id}`, {
          method: 'DELETE',
        });

        if (res.ok) {
          showSuccess('Космодесантник успешно удален');
        } else {
          showError('Ошибка при удалении космодесантника');
        }
      } catch (error) {
        console.error('Error deleting marine:', error);
        showError('Ошибка при удалении космодесантника');
      }
    }
  };

  // Валидация формы
  const validateForm = (): boolean => {
    if (!formData.name.trim()) {
      showError('Имя обязательно для заполнения');
      return false;
    }
    if (formData.health <= 0) {
      showError('Здоровье должно быть положительным числом');
      return false;
    }
    if (formData.height <= 0) {
      showError('Рост должен быть положительным числом');
      return false;
    }
    if (!formData.chapterId) {
      showError('Необходимо выбрать главу');
      return false;
    }
    return true;
  };

  // Сохранение (создание или обновление)
  const handleSave = async () => {
    if (!validateForm()) {
      return;
    }

    try {
      const url = editingMarine 
        ? `${API_BASE_URL}/api/space-marines/${editingMarine.id}`
        : `${API_BASE_URL}/api/space-marines`;

      const method = editingMarine ? 'PUT' : 'POST';

      // Подготавливаем данные для отправки (убираем null weaponType)
      const dataToSend = {
        ...formData,
        weaponType: formData.weaponType || undefined // преобразуем null в undefined
      };

      const res = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(dataToSend),
      });

      if (res.ok) {
        setDialogOpen(false);
        showSuccess(editingMarine ? 'Космодесантник успешно обновлен' : 'Космодесантник успешно создан');
      } else {
        const errorData = await res.json();
        const errorMessage = errorData.error || 'Неизвестная ошибка';
        showError(`Ошибка: ${errorMessage}`);
      }
    } catch (error) {
      console.error('Error saving marine:', error);
      showError('Ошибка при сохранении данных');
    }
  };

  // Обновление поля формы
  const handleFormChange = (field: keyof MarineFormData, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  };

  // Обновление координат
  const handleCoordinateChange = (coord: 'x' | 'y', value: string) => {
    setFormData(prev => ({
      ...prev,
      coordinates: {
        ...prev.coordinates,
        [coord]: parseFloat(value) || 0
      }
    }));
  };

  // Получить название главы по ID
  const getChapterName = (chapterId?: number): string => {
    if (!chapterId) return '—';
    const chapter = chapters.find(c => c.id === chapterId);
    return chapter ? chapter.name : '—';
  };

  // Форматирование названия оружия для отображения
  const formatWeaponName = (weaponType: string): string => {
    const weaponNames: Record<string, string> = {
      'HEAVY_BOLTGUN': 'Heavy Boltguns',
      'BOLT_PISTOL': 'Bolt Pistol',
      'PLASMA_GUN': 'Plasma Gun',
      'HEAVY_FLAMER': 'Heavy Flamer'
    };
    return weaponNames[weaponType] || weaponType;
  };

  return (
    <div className="p-4 space-y-4">
      {/* Уведомления об ошибках и успехах */}
      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}
      {success && (
        <Alert variant="default" className="bg-green-50 border-green-200">
          <AlertCircle className="h-4 w-4 text-green-600" />
          <AlertDescription className="text-green-800">{success}</AlertDescription>
        </Alert>
      )}

      {/* Статус WebSocket соединения */}
      <div className={`flex items-center space-x-2 p-2 rounded ${
        wsConnected ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
      }`}>
        {wsConnected ? <Wifi className="w-4 h-4" /> : <WifiOff className="w-4 h-4" />}
        <span className="text-sm font-medium">
          {wsConnected ? 'Синхронизация активна' : 'Синхронизация отключена'}
        </span>
      </div>

      <Card>
        <CardContent>
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-xl font-semibold">Space Marines</h2>
            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
              <DialogTrigger asChild>
                <Button onClick={handleCreate}>
                  <Plus className="w-4 h-4 mr-2" />
                  Add Marine
                </Button>
              </DialogTrigger>
              <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto bg-white border shadow-lg">
  {}
  <DialogHeader>
    <DialogTitle className="text-xl font-bold">
      {editingMarine ? 'Edit Space Marine' : 'Add New Space Marine'}
    </DialogTitle>
  </DialogHeader>
                
                {/* Сообщение об ошибке в диалоге */}
                {error && (
                  <Alert variant="destructive" className="mt-2">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{error}</AlertDescription>
                  </Alert>
                )}

                <div className="grid grid-cols-2 gap-4 py-4">
                  {/* Имя */}
                  <div className="space-y-2">
                    <Label htmlFor="name">Name *</Label>
                    <Input
                      id="name"
                      value={formData.name}
                      onChange={(e) => handleFormChange('name', e.target.value)}
                      placeholder="Enter marine name"
                    />
                  </div>

                  {/* Орден */}
                  <div className="space-y-2">
                    <Label htmlFor="chapter">Chapter *</Label>
                    <Select
                      value={formData.chapterId.toString()}
                      onValueChange={(value) => handleFormChange('chapterId', parseInt(value))}
                    >
                      <SelectTrigger className="bg-white">
                        <SelectValue placeholder="Select chapter" />
                      </SelectTrigger>
                      <SelectContent className="bg-white border shadow-lg">
                        {chapters.map((chapter) => (
                          <SelectItem key={chapter.id} value={chapter.id.toString()}>
                            {chapter.name}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  {/* Координата X */}
                  <div className="space-y-2">
                    <Label htmlFor="x">Coordinate X *</Label>
                    <Input
                      id="x"
                      type="number"
                      step="0.1"
                      value={formData.coordinates.x}
                      onChange={(e) => handleCoordinateChange('x', e.target.value)}
                    />
                  </div>

                  {/* Координата Y */}
                  <div className="space-y-2">
                    <Label htmlFor="y">Coordinate Y *</Label>
                    <Input
                      id="y"
                      type="number"
                      step="0.1"
                      value={formData.coordinates.y}
                      onChange={(e) => handleCoordinateChange('y', e.target.value)}
                    />
                  </div>

                  {/* Здоровье */}
                  <div className="space-y-2">
                    <Label htmlFor="health">Health *</Label>
                    <Input
                      id="health"
                      type="number"
                      min="1"
                      step="1"
                      value={formData.health}
                      onChange={(e) => handleFormChange('health', parseFloat(e.target.value) || 0)}
                    />
                  </div>

                  {/* Рост */}
                  <div className="space-y-2">
                    <Label htmlFor="height">Height *</Label>
                    <Input
                      id="height"
                      type="number"
                      min="1"
                      step="0.1"
                      value={formData.height}
                      onChange={(e) => handleFormChange('height', parseFloat(e.target.value) || 0)}
                    />
                  </div>

                  {/* Тип оружия */}
                  <div className="space-y-2">
                    <Label htmlFor="weaponType">Weapon Type</Label>
                    <Select
                      value={formData.weaponType || "NOT_SELECTED"}
                      onValueChange={(value) => handleFormChange('weaponType', value === "NOT_SELECTED" ? null : value)}
                    >
                      <SelectTrigger className="bg-white">
                        <SelectValue placeholder="Select weapon type" />
                      </SelectTrigger>
                      <SelectContent className="bg-white border shadow-lg">
                        <SelectItem value="NOT_SELECTED">Not selected</SelectItem>
                        {WeaponTypes.map((weapon) => (
                          <SelectItem key={weapon} value={weapon}>
                            {formatWeaponName(weapon)}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  {/* Достижения */}
                  <div className="space-y-2 col-span-2">
                    <Label htmlFor="achievements">Achievements</Label>
                    <Input
                      id="achievements"
                      value={formData.achievements}
                      onChange={(e) => handleFormChange('achievements', e.target.value)}
                      placeholder="Enter marine achievements"
                    />
                  </div>
                </div>
                <div className="flex justify-end space-x-2">
                  <Button variant="outline" onClick={() => setDialogOpen(false)}>
                    Cancel
                  </Button>
                  <Button onClick={handleSave}>
                    {editingMarine ? 'Update' : 'Create'}
                  </Button>
                </div>
              </DialogContent>
            </Dialog>
          </div>

          {/* 🔍 Фильтры */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-2 mb-3">
            <Input
              placeholder="Filter by name"
              value={filters.name}
              onChange={(e) => handleFilterChange("name", e.target.value)}
            />
            <Input
              placeholder="Filter by achievements"
              value={filters.achievements}
              onChange={(e) => handleFilterChange("achievements", e.target.value)}
            />
            <Button onClick={applyFilters}>Apply Filters</Button>
          </div>

          {/* 🧾 Таблица */}
          <div className="overflow-x-auto">
            <table className="min-w-full text-sm border">
              <thead className="bg-gray-100">
                <tr>
                  {[
                    "id",
                    "name",
                    "coordinates.x",
                    "coordinates.y",
                    "creationDate",
                    "chapter.name",
                    "chapter.id",
                    "health",
                    "achievements",
                    "height",
                    "weaponType",
                    "actions"
                  ].map((col) => (
                    <th
                      key={col}
                      className="px-3 py-2 border cursor-pointer hover:bg-gray-200"
                      onClick={() => col !== 'actions' && handleSort(col)}
                    >
                      {col === 'actions' ? 'Actions' : col}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {!loading && marines.length > 0 ? (
                  marines.map((m) => (
                    <tr key={m.id} className="border-b hover:bg-gray-50">
                      <td className="px-3 py-2">{m.id}</td>
                      <td className="px-3 py-2">{m.name}</td>
                      <td className="px-3 py-2">{m.coordinates?.x}</td>
                      <td className="px-3 py-2">{m.coordinates?.y}</td>
                      <td className="px-3 py-2">
                        {m.creationDate ? new Date(m.creationDate).toLocaleString() : '—'}
                      </td>
                      {/* Используем название главы из данных или находим по ID */}
                      <td className="px-3 py-2">{m.chapterName || getChapterName(m.chapterId)}</td>
                      <td className="px-3 py-2">{m.chapterId || '—'}</td>
                      <td className="px-3 py-2">{m.health}</td>
                      <td className="px-3 py-2">{m.achievements}</td>
                      <td className="px-3 py-2">{m.height}</td>
                      <td className="px-3 py-2">{m.weaponType ? formatWeaponName(m.weaponType) : "—"}</td>
                      <td className="px-3 py-2 border text-center">
                        <div className="flex justify-center space-x-2">
                          <Button
                            variant="outline"
                            size="sm"
                            onClick={() => handleEdit(m)}
                          >
                            <Edit className="w-4 h-4" />
                          </Button>
                          <Button
                            variant="destructive"
                            size="sm"
                            onClick={() => handleDelete(m.id)}
                          >
                            <Trash2 className="w-4 h-4" />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))
                ) : (
                  !loading && (
                    <tr>
                      <td colSpan={12} className="text-center p-4">
                        No data found
                      </td>
                    </tr>
                  )
                )}
                {loading && (
                  <tr>
                    <td colSpan={12} className="text-center p-4">
                      Loading...
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>

          {/* 📄 Пагинация */}
          <div className="flex justify-between items-center mt-4">
            <div className="space-x-2">
              <Button
                variant="outline"
                disabled={page === 0}
                onClick={() => setPage((p) => Math.max(0, p - 1))}
              >
                <ChevronLeft className="w-4 h-4" /> Prev
              </Button>
              <Button
                variant="outline"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Next <ChevronRight className="w-4 h-4" />
              </Button>
            </div>
            <span>
              Page {page + 1} of {totalPages || 1}
            </span>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}