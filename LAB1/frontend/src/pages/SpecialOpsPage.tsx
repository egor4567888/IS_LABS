import React, { useEffect, useState, useCallback } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Card, CardContent } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { AlertCircle, Plus, Trash2, RefreshCw, Wifi, WifiOff } from "lucide-react";
import { webSocketService } from "@/services/websocketService";

// Типы для TypeScript
interface AchievementGroup {
  achievements: string;
  cnt: number;
}

interface Chapter {
  id: number;
  name: string;
}

interface SpaceMarine {
  id: number;
  name: string;
  health: number;
  achievements: string;
  weaponType?: string;
  chapterId?: number;
}

interface WebSocketMessage {
  action: 'create' | 'update' | 'delete';
  id: number;
  type?: 'marine' | 'chapter';
}

// Enum для типов оружия
const WeaponTypes = [
  "HEAVY_BOLTGUN",
  "BOLT_PISTOL", 
  "PLASMA_GUN",
  "HEAVY_FLAMER"
] as const;

export default function SpecialOpsPage() {
  // Состояния для группировки и подсчетов
  const [achievementGroups, setAchievementGroups] = useState<AchievementGroup[]>([]);
  const [weaponCount, setWeaponCount] = useState<number | null>(null);
  const [healthCount, setHealthCount] = useState<number | null>(null);
  const [allMarines, setAllMarines] = useState<SpaceMarine[]>([]);
  
  // Состояния для форм
  const [selectedWeapon, setSelectedWeapon] = useState<string>("");
  const [healthThreshold, setHealthThreshold] = useState<string>("");
  const [newChapterName, setNewChapterName] = useState<string>("");
  const [newChapterCount, setNewChapterCount] = useState<string>("");
  const [chapterToDelete, setChapterToDelete] = useState<string>("");
  
  // Состояния для UI
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [wsConnected, setWsConnected] = useState(false);
  const [chapters, setChapters] = useState<Chapter[]>([]);

  const API_BASE_URL = 'http://localhost:8080';

  // Загрузка всех данных
  const loadAllData = useCallback(async () => {
    setLoading(true);
    try {
      await Promise.all([
        fetchAchievementGroups(),
        fetchAllMarines(),
        fetchChapters()
      ]);
    } catch (error) {
      console.error('Error loading data:', error);
      showError('Ошибка загрузки данных');
    } finally {
      setLoading(false);
    }
  }, []);

  // Загрузка группировки по achievements
  const fetchAchievementGroups = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/special/group-by-achievements`);
      if (res.ok) {
        const data: AchievementGroup[] = await res.json();
        setAchievementGroups(data);
      } else {
        throw new Error('Failed to fetch achievement groups');
      }
    } catch (error) {
      console.error('Error fetching achievement groups:', error);
      throw error;
    }
  };

  // Загрузка всех космодесантников
  const fetchAllMarines = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/special/all-marines`);
      if (res.ok) {
        const data: SpaceMarine[] = await res.json();
        setAllMarines(data);
      } else {
        throw new Error('Failed to fetch all marines');
      }
    } catch (error) {
      console.error('Error fetching all marines:', error);
      throw error;
    }
  };

  // Загрузка списка глав
  const fetchChapters = async () => {
    try {
      const res = await fetch(`${API_BASE_URL}/api/chapters`);
      if (res.ok) {
        const data: Chapter[] = await res.json();
        setChapters(data);
      }
    } catch (error) {
      console.error('Error fetching chapters:', error);
    }
  };

  // Подсчет оружия меньше заданного
  const fetchWeaponCount = async (weapon: string) => {
    if (!weapon) return;
    
    try {
      const res = await fetch(`${API_BASE_URL}/api/special/count-weapon-less-than?weapon=${encodeURIComponent(weapon)}`);
      if (res.ok) {
        const count: number = await res.json();
        setWeaponCount(count);
        showSuccess(`Количество оружия меньше ${weapon}: ${count}`);
      } else {
        throw new Error('Failed to fetch weapon count');
      }
    } catch (error) {
      console.error('Error fetching weapon count:', error);
      showError('Ошибка при подсчете оружия');
    }
  };

  // Подсчет здоровья больше заданного
  const fetchHealthCount = async (threshold: string) => {
    const thresholdNum = parseInt(threshold);
    if (isNaN(thresholdNum)) return;
    
    try {
      const res = await fetch(`${API_BASE_URL}/api/special/count-health-greater-than?threshold=${thresholdNum}`);
      if (res.ok) {
        const count: number = await res.json();
        setHealthCount(count);
        showSuccess(`Количество космодесантников с здоровьем > ${thresholdNum}: ${count}`);
      } else {
        throw new Error('Failed to fetch health count');
      }
    } catch (error) {
      console.error('Error fetching health count:', error);
      showError('Ошибка при подсчете здоровья');
    }
  };

  // Создание нового ордена
  const createChapter = async () => {
    if (!newChapterName.trim() || !newChapterCount.trim()) {
      showError('Введите название ордена и количество');
      return;
    }

    const count = parseInt(newChapterCount);
    if (isNaN(count) || count <= 0) {
      showError('Количество должно быть положительным числом');
      return;
    }

    try {
      const res = await fetch(`${API_BASE_URL}/api/chapters/create-via-db?name=${encodeURIComponent(newChapterName)}&count=${count}`, {
        method: 'POST'
      });

      if (res.ok) {
        const chapterId = await res.json();
        showSuccess(`Орден "${newChapterName}" создан с ID: ${chapterId}`);
        setNewChapterName("");
        setNewChapterCount("");
        // Данные автоматически обновятся через WebSocket
      } else {
        throw new Error('Failed to create chapter');
      }
    } catch (error) {
      console.error('Error creating chapter:', error);
      showError('Ошибка при создании ордена');
    }
  };

  // Удаление ордена
  const deleteChapter = async () => {
    if (!chapterToDelete.trim()) {
      showError('Введите ID ордена для удаления');
      return;
    }

    const chapterId = parseInt(chapterToDelete);
    if (isNaN(chapterId)) {
      showError('ID ордена должен быть числом');
      return;
    }

    if (!window.confirm(`Вы уверены, что хотите удалить орден с ID ${chapterId}? Это может удалить связанных космодесантников!`)) {
      return;
    }

    try {
      const res = await fetch(`${API_BASE_URL}/api/chapters/dissolve-via-db/${chapterId}`, {
        method: 'DELETE'
      });

      if (res.ok) {
        showSuccess(`Орден с ID ${chapterId} удален`);
        setChapterToDelete("");
        // Данные автоматически обновятся через WebSocket
      } else {
        throw new Error('Failed to delete chapter');
      }
    } catch (error) {
      console.error('Error deleting chapter:', error);
      showError('Ошибка при удалении ордена');
    }
  };

  // Обработчик WebSocket сообщений для космодесантников
  const handleMarineWebSocketMessage = useCallback((message: WebSocketMessage) => {
    console.log('Marine WebSocket update received in SpecialOps:', message);
    showSuccess(`Синхронизация: ${message.action} космодесантника ID ${message.id}`);
    
    // Перезагружаем данные при любом изменении космодесантников
    loadAllData();
  }, [loadAllData]);

  // Обработчик WebSocket сообщений для орденов
  const handleChapterWebSocketMessage = useCallback((message: WebSocketMessage) => {
    console.log('Chapter WebSocket update received in SpecialOps:', message);
    showSuccess(`Синхронизация: ${message.action} ордена ID ${message.id}`);
    
    // Перезагружаем данные при любом изменении орденов
    loadAllData();
  }, [loadAllData]);

  // Обработчик изменения состояния подключения WebSocket
  const handleConnectionChange = useCallback((connected: boolean) => {
    console.log('WebSocket connection status changed:', connected);
    setWsConnected(connected);
  }, []);

  // Инициализация WebSocket
  useEffect(() => {
    console.log('Initializing WebSocket for SpecialOps...');
    
    // Подписываемся на изменения подключения
    webSocketService.onConnectionChange(handleConnectionChange);
    
    // Подписываемся на обновления космодесантников и орденов
    webSocketService.subscribe('/topic/spaceMarines', handleMarineWebSocketMessage);
    webSocketService.subscribe('/topic/chapters', handleChapterWebSocketMessage);
    
    // Подключаемся к WebSocket
    webSocketService.connect();

    return () => {
      console.log('Cleaning up WebSocket for SpecialOps...');
      webSocketService.offConnectionChange(handleConnectionChange);
      webSocketService.unsubscribe('/topic/spaceMarines', handleMarineWebSocketMessage);
      webSocketService.unsubscribe('/topic/chapters', handleChapterWebSocketMessage);
    };
  }, [handleMarineWebSocketMessage, handleChapterWebSocketMessage, handleConnectionChange]);

  // Загрузка данных при монтировании компонента
  useEffect(() => {
    loadAllData();
  }, [loadAllData]);

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

  // Получить количество космодесантников по ордену
  const getMarinesCountByChapter = (chapterId: number): number => {
    return allMarines.filter(marine => marine.chapterId === chapterId).length;
  };

  return (
    <div className="p-4 space-y-6">
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
      <div className={`flex items-center justify-between p-3 rounded ${
        wsConnected ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'
      }`}>
        <div className="flex items-center space-x-2">
          {wsConnected ? <Wifi className="w-4 h-4" /> : <WifiOff className="w-4 h-4" />}
          <span className="text-sm font-medium">
            {wsConnected ? 'Синхронизация активна' : 'Подключение...'}
          </span>
        </div>
        <div className="flex space-x-2">
          <Button 
            variant="outline" 
            size="sm" 
            onClick={loadAllData}
            disabled={loading}
          >
            <RefreshCw className="w-4 h-4 mr-1" />
            Обновить данные
          </Button>
          <Button 
            variant="outline" 
            size="sm" 
            onClick={handleReconnect}
          >
            <RefreshCw className="w-4 h-4 mr-1" />
            Переподключить
          </Button>
        </div>
      </div>

      {/* Основной контент в grid layout */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        
        {/* Левая колонка: Группировка и подсчеты */}
        <div className="space-y-6">
          
          {/* Группировка по achievements */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Группировка по достижениям</h3>
              {achievementGroups.length > 0 ? (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Достижения</TableHead>
                      <TableHead className="text-right">Количество</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {achievementGroups.map((group, index) => (
                      <TableRow key={index}>
                        <TableCell className="font-medium">
                          {group.achievements || 'Без достижений'}
                        </TableCell>
                        <TableCell className="text-right">
                          <Badge variant="secondary">{group.cnt}</Badge>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              ) : (
                <p className="text-muted-foreground">Нет данных для отображения</p>
              )}
            </CardContent>
          </Card>

          {/* Подсчет оружия */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Подсчет оружия</h3>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="weapon-select">Тип оружия для сравнения</Label>
                  <Select value={selectedWeapon} onValueChange={setSelectedWeapon}>
                    <SelectTrigger className="bg-white">
                      <SelectValue placeholder="Выберите тип оружия" />
                    </SelectTrigger>
                    <SelectContent className="bg-white">
                      {WeaponTypes.map((weapon) => (
                        <SelectItem key={weapon} value={weapon}>
                          {formatWeaponName(weapon)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <Button 
                  onClick={() => fetchWeaponCount(selectedWeapon)}
                  disabled={!selectedWeapon}
                >
                  Подсчитать оружие меньше выбранного
                </Button>
                {weaponCount !== null && (
                  <div className="p-3 bg-blue-50 rounded-md">
                    <p className="text-blue-800 font-medium">
                      Количество оружия меньше {formatWeaponName(selectedWeapon)}: {weaponCount}
                    </p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Подсчет здоровья */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Подсчет здоровья</h3>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="health-threshold">Порог здоровья</Label>
                  <Input
                    id="health-threshold"
                    type="number"
                    placeholder="Введите пороговое значение здоровья"
                    value={healthThreshold}
                    onChange={(e) => setHealthThreshold(e.target.value)}
                    className="bg-white"
                  />
                </div>
                <Button 
                  onClick={() => fetchHealthCount(healthThreshold)}
                  disabled={!healthThreshold}
                >
                  Подсчитать здоровье больше порога
                </Button>
                {healthCount !== null && (
                  <div className="p-3 bg-green-50 rounded-md">
                    <p className="text-green-800 font-medium">
                      Количество с здоровьем больше {healthThreshold}: {healthCount}
                    </p>
                  </div>
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Правая колонка: Управление орденами и общая информация */}
        <div className="space-y-6">
          
          {/* Создание нового ордена */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Создание нового ордена</h3>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="chapter-name">Название ордена</Label>
                  <Input
                    id="chapter-name"
                    placeholder="Введите название ордена"
                    value={newChapterName}
                    onChange={(e) => setNewChapterName(e.target.value)}
                    className="bg-white"
                  />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="chapter-count">Количество космодесантников</Label>
                  <Input
                    id="chapter-count"
                    type="number"
                    placeholder="Введите количество"
                    value={newChapterCount}
                    onChange={(e) => setNewChapterCount(e.target.value)}
                    className="bg-white"
                  />
                </div>
                <Button onClick={createChapter} className="w-full">
                  <Plus className="w-4 h-4 mr-2" />
                  Создать орден
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* Удаление ордена */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Удаление ордена</h3>
              <div className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="chapter-delete">ID ордена для удаления</Label>
                  <Input
                    id="chapter-delete"
                    type="number"
                    placeholder="Введите ID ордена"
                    value={chapterToDelete}
                    onChange={(e) => setChapterToDelete(e.target.value)}
                    className="bg-white"
                  />
                </div>
                <Button onClick={deleteChapter} variant="destructive" className="w-full">
                  <Trash2 className="w-4 h-4 mr-2" />
                  Удалить орден
                </Button>
              </div>
            </CardContent>
          </Card>

          {/* Список существующих орденов */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Существующие ордены</h3>
              {chapters.length > 0 ? (
                <div className="space-y-2">
                  {chapters.map((chapter) => (
                    <div key={chapter.id} className="flex justify-between items-center p-2 border rounded">
                      <div>
                        <span className="font-medium">{chapter.name}</span>
                        <Badge variant="secondary" className="ml-2">
                          {getMarinesCountByChapter(chapter.id)} космодесантников
                        </Badge>
                      </div>
                      <Badge variant="outline">ID: {chapter.id}</Badge>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-muted-foreground">Нет загруженных орденов</p>
              )}
            </CardContent>
          </Card>

          {/* Общая статистика */}
          <Card>
            <CardContent className="pt-6">
              <h3 className="text-lg font-semibold mb-4">Общая статистика</h3>
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span>Всего космодесантников:</span>
                  <Badge>{allMarines.length}</Badge>
                </div>
                <div className="flex justify-between">
                  <span>Всего орденов:</span>
                  <Badge>{chapters.length}</Badge>
                </div>
                <div className="flex justify-between">
                  <span>Групп достижений:</span>
                  <Badge>{achievementGroups.length}</Badge>
                </div>
                <div className="flex justify-between">
                  <span>WebSocket статус:</span>
                  <Badge variant={wsConnected ? "default" : "secondary"}>
                    {wsConnected ? 'Подключен' : 'Отключен'}
                  </Badge>
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}